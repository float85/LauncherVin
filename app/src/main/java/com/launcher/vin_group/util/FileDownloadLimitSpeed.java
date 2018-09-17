package com.launcher.vin_group.util;

import com.google.common.util.concurrent.RateLimiter;
import com.liulishuo.filedownloader.stream.FileDownloadOutputStream;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;



public class FileDownloadLimitSpeed implements FileDownloadOutputStream {
    private final BufferedOutputStream out;
    private final FileDescriptor fd;
    private final RandomAccessFile randomAccess;
    private RateLimiter rateLimiter;

    Random random;

    FileDownloadLimitSpeed(File file) throws IOException {
        randomAccess = new RandomAccessFile(file, "rw");
        fd = randomAccess.getFD();
        out = new BufferedOutputStream(new FileOutputStream(randomAccess.getFD()));
        rateLimiter = RateLimiter.create(MyCons.LIMIT_SPEED);

        random = new Random();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        rateLimiter.acquire(b.length);
        out.write(b, off, len);
    }

    @Override
    public void flushAndSync() throws IOException {
        out.flush();
        fd.sync();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void seek(long offset) throws IOException {
        randomAccess.seek(offset);
    }

    @Override
    public void setLength(long totalBytes) throws IOException {
        randomAccess.setLength(totalBytes);
    }

    public static class Creator implements FileDownloadHelper.OutputStreamCreator {

        @Override
        public FileDownloadOutputStream create(File file) throws IOException {
            return new FileDownloadLimitSpeed(file);
        }

        @Override
        public boolean supportSeek() {
            return true;
        }
    }
}