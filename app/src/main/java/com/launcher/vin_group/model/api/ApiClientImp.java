package com.launcher.vin_group.model.api;

//import com.google.common.base.Converter;

import com.facebook.stetho.inspector.network.DefaultResponseHandler;
import com.facebook.stetho.inspector.network.NetworkEventReporter;
import com.facebook.stetho.inspector.network.NetworkEventReporterImpl;
import com.facebook.stetho.inspector.network.RequestBodyHelper;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.launcher.vin_group.App;
import com.launcher.vin_group.R;
import com.launcher.vin_group.model.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Connection;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClientImp {

//    public static final String API_URL = "https://cmsmkt-devtest.vingroup.net/api/";
    public static final String API_URL = App.getInstance().getString(R.string.url);
//    public static final String API_URL = "https://cmsmkt.vingroup.net/api/";
//    public static final String API_URL = "https://demo4413932.mockable.io/";

    static ApiClient apiclient;

    public static ApiClient getInstance() {
        if (apiclient == null) {
//            OkHttpClient okHttpClient = newOkHttpInstance();
            OkHttpClient okHttpClient = newUnsafeOkHttpInstance ();

            Retrofit restAdapter = newRetrofitInstance(API_URL, okHttpClient);
            apiclient = restAdapter.create(ApiClient.class);
        }

        return apiclient;
    }

    private static Retrofit newRetrofitInstance(String endPoint, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private static OkHttpClient newOkHttpInstance() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new CustomInterceptor())
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private static OkHttpClient newUnsafeOkHttpInstance() {
        try {
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

            OkHttpClient okHttpClient = builder
                    .addNetworkInterceptor(new CustomInterceptor())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class CustomInterceptor extends StethoInterceptor {
        private final NetworkEventReporter mEventReporter = NetworkEventReporterImpl.get();

        private final AtomicInteger mNextRequestId = new AtomicInteger(0);

        @Override
        public Response intercept(Chain chain) throws IOException {
            String requestId = String.valueOf(mNextRequestId.getAndIncrement());

            Request request = chain.request();

            RequestBodyHelper requestBodyHelper = null;
            if (mEventReporter.isEnabled()) {
                requestBodyHelper = new RequestBodyHelper(mEventReporter, requestId);
                OkHttpInspectorRequest inspectorRequest =
                        new OkHttpInspectorRequest(requestId, request, requestBodyHelper);
                mEventReporter.requestWillBeSent(inspectorRequest);
            }

            Response response;
            try {
                response = chain.proceed(request);
            } catch (IOException e) {
                if (mEventReporter.isEnabled()) {
                    mEventReporter.httpExchangeFailed(requestId, e.toString());
                }
                throw e;
            }

            if (mEventReporter.isEnabled()) {
                if (requestBodyHelper != null && requestBodyHelper.hasBody()) {
                    requestBodyHelper.reportDataSent();
                }

                Connection connection = chain.connection();
                mEventReporter.responseHeadersReceived(
                        new OkHttpInspectorResponse(
                                requestId,
                                request,
                                response,
                                connection));

                ResponseBody body = response.body();
                MediaType contentType = null;
                InputStream responseStream = null;
                if (body != null) {
                    contentType = body.contentType();
                    responseStream = body.byteStream();
                }

                responseStream = mEventReporter.interpretResponseStream(
                        requestId,
                        contentType != null ? contentType.toString() : null,
                        response.header("Content-Encoding"),
                        responseStream,
                        new DefaultResponseHandler(mEventReporter, requestId));
                if (responseStream != null) {
                    response = response.newBuilder()
                            .body(new ForwardingResponseBody(body, responseStream))
                            .build();
                }
            }

            return response;
        }

        private static class OkHttpInspectorRequest implements NetworkEventReporter.InspectorRequest {
            private final String mRequestId;
            private final Request mRequest;
            private RequestBodyHelper mRequestBodyHelper;

            public OkHttpInspectorRequest(
                    String requestId,
                    Request request,
                    RequestBodyHelper requestBodyHelper) {
                mRequestId = requestId;
                mRequest = request;
                mRequestBodyHelper = requestBodyHelper;
            }

            @Override
            public String id() {
                return mRequestId;
            }

            @Override
            public String friendlyName() {
                // Hmm, can we do better?  tag() perhaps?
                return null;
            }

            @Nullable
            @Override
            public Integer friendlyNameExtra() {
                return null;
            }

            @Override
            public String url() {
                return mRequest.url().url().getPath();
            }

            @Override
            public String method() {
                return mRequest.method();
            }

            @Nullable
            @Override
            public byte[] body() throws IOException {
                RequestBody body = mRequest.body();
                if (body == null) {
                    return null;
                }
                OutputStream out = mRequestBodyHelper.createBodySink(firstHeaderValue("Content-Encoding"));
                BufferedSink bufferedSink = Okio.buffer(Okio.sink(out));
                try {
                    body.writeTo(bufferedSink);
                } finally {
                    bufferedSink.close();
                }
                return mRequestBodyHelper.getDisplayBody();
            }

            @Override
            public int headerCount() {
                return mRequest.headers().size();
            }

            @Override
            public String headerName(int index) {
                return mRequest.headers().name(index);
            }

            @Override
            public String headerValue(int index) {
                return mRequest.headers().value(index);
            }

            @Nullable
            @Override
            public String firstHeaderValue(String name) {
                return mRequest.header(name);
            }
        }

        private static class OkHttpInspectorResponse implements NetworkEventReporter.InspectorResponse {
            private final String mRequestId;
            private final Request mRequest;
            private final Response mResponse;
            private final Connection mConnection;

            public OkHttpInspectorResponse(
                    String requestId,
                    Request request,
                    Response response,
                    Connection connection) {
                mRequestId = requestId;
                mRequest = request;
                mResponse = response;
                mConnection = connection;
            }

            @Override
            public String requestId() {
                return mRequestId;
            }

            @Override
            public String url() {
                return mRequest.url().url().getPath();
            }

            @Override
            public int statusCode() {
                return mResponse.code();
            }

            @Override
            public String reasonPhrase() {
                return mResponse.message();
            }

            @Override
            public boolean connectionReused() {
                // Not sure...
                return false;
            }

            @Override
            public int connectionId() {
                return mConnection.hashCode();
            }

            @Override
            public boolean fromDiskCache() {
                return mResponse.cacheResponse() != null;
            }

            @Override
            public int headerCount() {
                return mResponse.headers().size();
            }

            @Override
            public String headerName(int index) {
                return mResponse.headers().name(index);
            }

            @Override
            public String headerValue(int index) {
                return mResponse.headers().value(index);
            }

            @Nullable
            @Override
            public String firstHeaderValue(String name) {
                return "application/json";
            }
        }

        private static class ForwardingResponseBody extends ResponseBody {
            private final ResponseBody mBody;
            private final BufferedSource mInterceptedSource;

            public ForwardingResponseBody(ResponseBody body, InputStream interceptedStream) {
                mBody = body;
                mInterceptedSource = Okio.buffer(Okio.source(interceptedStream));
            }

            @Override
            public MediaType contentType() {
                return mBody.contentType();
            }

            @Override
            public long contentLength() {
                return mBody.contentLength();
            }

            @Override
            public BufferedSource source() {
                // close on the delegating body will actually close this intercepted source, but it
                // was derived from mBody.byteStream() therefore the close will be forwarded all the
                // way to the original.
                return mInterceptedSource;
            }
        }
    }

}
