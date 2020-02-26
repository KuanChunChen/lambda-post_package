package com.amazonaws.lambda.demo.http.interceptor;

//import android.ctms_service.Log.LogTool;

import java.io.IOException;
import java.io.InterruptedIOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryRequestByFaild implements Interceptor {
    public int executionCount;
    private long retryInterval;


    RetryRequestByFaild(Builder builder) {
        this.executionCount = builder.executionCount;
        this.retryInterval = builder.retryInterval;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = doRequest(chain, request);
        int retryNum = 0;
        while ((response == null || !response.isSuccessful()) && retryNum <= executionCount) {
//            LogTool.d("intercept Request is not successful - %d",retryNum);
            final long nextInterval = getRetryInterval();
            try {
//                LogTool.d("Wait for %s", nextInterval);
                Thread.sleep(nextInterval);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
            retryNum++;
            // retry the request
            response = doRequest(chain, request);
        }
        return response;
    }

    private Response doRequest(Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
        }
        return response;
    }

    /**
     * retry interval
     */
    public long getRetryInterval() {
        return this.retryInterval;
    }

    public static final class Builder {
        private int executionCount;
        private long retryInterval;
        public Builder() {
            executionCount = 3;
            retryInterval = 1000;
        }

        public RetryRequestByFaild.Builder executionCount(int executionCount){
            this.executionCount =executionCount;
            return this;
        }

        public RetryRequestByFaild.Builder retryInterval(long retryInterval){
            this.retryInterval =retryInterval;
            return this;
        }
        public RetryRequestByFaild build() {
            return new RetryRequestByFaild(this);
        }
    }

}
