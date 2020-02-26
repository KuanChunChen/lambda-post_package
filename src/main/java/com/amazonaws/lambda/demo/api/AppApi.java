package com.amazonaws.lambda.demo.api;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface AppApi {

//
//    @Headers({
//            "Connection: keep-alive",
//            "Content-Type: multipart/form-data; boundary=YY8CBaVruPGDVTpIhyxGBTcF_oY_xOleL",
//            "Accept: text/html,appli.cation/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
//            "Accept-Language: fr,en;q=0.8,fr-fr;q=0.5,en-us;q=0.3"
//    })
    @POST("/api/api_delivery.php")
    Call<String> postCSV(@Body RequestBody requestBody);

}

