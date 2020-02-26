package com.amazonaws.lambda.demo.http;





import java.util.concurrent.TimeUnit;

import com.amazonaws.lambda.demo.api.AppApi;
import com.amazonaws.lambda.demo.constants.Constants;
import com.amazonaws.lambda.demo.converter.ToStringConverterFactory;
import com.amazonaws.lambda.demo.http.interceptor.RetryRequestByFaild;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;





public class RetrofitClient {

    private static OkHttpClient okHttpClient;


    private static AppApi csvClient;


    static {


        OkHttpClient.Builder okBuilder = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new RetryRequestByFaild.Builder().build())
                .retryOnConnectionFailure(true)
                ;

//        HttpLoggingInterceptor loggingInterceptor = null;
//        if (BuildConfig.LOG_DEBUG || AppSingle.getInstance().getDisturbLog().equals(DLAConstants.DISTURB_LOG_WITH_OPEN)) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        //add the log of okhttp
        okBuilder.addInterceptor(loggingInterceptor);
//        }
        okHttpClient = okBuilder.connectionPool(new ConnectionPool(0, 1, TimeUnit.SECONDS)).build();
    }

  

    public static AppApi httpCSV(String hostName) {
        if (csvClient == null) {
            synchronized (RetrofitClient.class) {
                csvClient = new Retrofit.Builder()
                        .baseUrl(hostName)
                        .client(okHttpClient)
                        .addConverterFactory(new ToStringConverterFactory())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(AppApi.class);

            }
        }
        return csvClient;
    }

    public static AppApi httpCSV() {
        return httpCSV(Constants.Http.HTTP_SERVER_TARGET_CONN_URL);
    }

 

}
