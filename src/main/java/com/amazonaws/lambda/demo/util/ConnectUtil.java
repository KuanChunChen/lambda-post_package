package com.amazonaws.lambda.demo.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.amazonaws.lambda.demo.constants.Constants;

import com.amazonaws.lambda.demo.http.RetrofitClient;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ConnectUtil{


	public static void sentDelivery(String bucket,String key,String strFileName,String strCSVConText){


		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
		strCSVConText = s3Client.getObjectAsString(bucket, key)
		.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1")
		.replaceAll("^((\r\n)|\n)", "").trim();


		RequestBody requestBody = new MultipartBody.Builder()
		.setType(MultipartBody.FORM)
		.addFormDataPart("CMD",Constants.Package.POST_REQUEST_BODY)
		.addFormDataPart("Upload", strFileName, RequestBody.create(MediaType.parse("text/csv"),strCSVConText))
		.build();

		System.out.print(Constants.Message.PROCESS_POST);


		Call<String> call = RetrofitClient.httpCSV().postCSV(requestBody);

		try {
			Response<String> response = call.execute();
			System.out.print("My response : \r\n"+response.body().toString()+"\r\n");


		} catch (IOException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

}