package com.launcher.vin_group;

import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.bosphere.filelogger.FL;
import com.bosphere.filelogger.FLConfig;
import com.bosphere.filelogger.FLConst;
import com.facebook.stetho.Stetho;
import com.launcher.vin_group.model.db.Db;
import com.launcher.vin_group.util.FileDownloadLimitSpeed;
import com.launcher.vin_group.util.MyCons;
import com.launcher.vin_group.view.screen.crash_screen.CrashActivity;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;

import java.io.File;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import cn.dreamtobe.filedownloader.OkHttp3Connection;
import okhttp3.OkHttpClient;


public class App extends MultiDexApplication {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    public static boolean isLiveStreamMode = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Stetho.initializeWithDefaults(this);
        Db.init(this);
        MultiDex.install(this);

        configDownload();
        configCrash();
        configLogger();
    }

    void configDownload(){
        try{
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker()
                    .outputStreamCreator(new FileDownloadLimitSpeed.Creator())
                    .connectionCreator(new OkHttp3Connection.Creator(builder)));

        }catch (Exception e){

        }
    }

    private void configLogger() {
        FL.init(new FLConfig.Builder(this)
                .defaultTag(MyCons.LOG)
                .logToFile(true)
                .dir(new File(Environment.getExternalStorageDirectory(), "file_logger_demo"))
                .retentionPolicy(FLConst.RetentionPolicy.FILE_COUNT)
                .build());
        FL.setEnabled(true);
    }

    private void configCrash() {
        CaocConfig.Builder.create()
                .errorActivity(CrashActivity.class)
                .showErrorDetails(true)
                .showRestartButton(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(3000)
                .apply();
    }

    public String getStoragePath() {
        return Environment.getExternalStorageDirectory() + "/Adtruevn";
    }
}
