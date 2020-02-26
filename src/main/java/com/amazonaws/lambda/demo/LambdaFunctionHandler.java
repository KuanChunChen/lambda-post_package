package com.amazonaws.lambda.demo;



import java.io.IOException;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.amazonaws.lambda.demo.http.RetrofitClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
/*
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
*/

/**
 * Created by Willy Chen on 2019/07/10.
 * @feature Use this lambda function to upload csv to server.
 * @author Willy Chen
 * @version v1.0.1
 */


public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

	/*
	 BasicAWSCredentials creds = new BasicAWSCredentials("aws-access-key", "aws-secret-key");

	    private AmazonS3 s3 =AmazonS3Client.builder()
	    	    .withRegion("ap-northeast-1")
	    	    .withCredentials(new AWSStaticCredentialsProvider(creds))
	    	    .build();

	    Region region = Region.getRegion(Regions.AP_NORTHEAST_1);

    public LambdaFunctionHandler() {}

    // Test purpose only.
    LambdaFunctionHandler(AmazonS3 s3) {
        this.s3 = s3;
    }
	*/
	
	/* **define value ** */
	/* *Check file name* */
	//private static final int d_CSV_FILE_NAME_LEN = 34;
	private static final int d_CSV_FILE_DATE_LEN = 17;
	//private static final int d_CSV_FILE_SUFFIX_LENGTH = 4;
	private static final int d_CSV_FILE_PREFIX_LENGTH = 18;
	private static final String d_CSV_FILE_PREFIX = "FromArch/deliInst_";
	private static final String d_CSV_FILE_SUFFIX = ".csv";
	private static final String d_CSV_FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
	
	/* *Error Code* */
	private static final byte d_OK = 0x00;
	private static final byte d_DATE_FORMAT_ERROR = 0x01;
	private static final byte d_FILE_LEN_ERROR = 0x02;
	private static final byte d_FILE_PREFIX_ERROR = 0x03;
	private static final byte d_FILE_SUFFIX_ERROR = 0x04;
	private static final byte d_FILE_DATE_LEN_ERROR = 0x05;
	
	



	private static  final String d_POST_BODY_USERNAME = "lambda";
	private static  final String d_POST_BODY_PASSWORD = "castles8418";
	private static  final String d_POST_BODY_FUNCTION_NAME = "CREATEDELIVERY";
	private static  final String d_POST_BODY_CREATEMODE = "2";


	/* **Process ** */
	 
	private static  final String d_TAG_CHECK_FILENAME = "CHECK FILE NAME：";
	private static  final String d_PROCESS_CHECK_FILE_NAME = "------Strat Checking file name------";
	private static  final String d_PROCESS_CHECK_END = "------Check File Name End------";
	private static  final String d_PROCESS_CHECK_CORRECT = "---File Name Correct---";
	private static  final String d_PROCESS_POST = "------Start Post to URL------";


	
	 
	private static String d_STATUS = "Return Code:";
	private  String strCSVConText;
	private String strFileName;

	/* **handle ** */
	/* **lambda process beginning point ** */
    @Override
    public String handleRequest(S3Event event, Context context) {
    	
    	byte bReturnCode;
    	
        //context.getLogger().log("Received event: " + event);
        //context.getLogger().log("Event Detail: " + event.toJson());
        // Get the object from the event and show its content type
        String bucket =null;
        String key =null;
        
        try {
        	bucket = event.getRecords().get(0).getS3().getBucket().getName();
            key = event.getRecords().get(0).getS3().getObject().getKey();
            strFileName=null;
        }
        catch(Exception e) {
        	e.printStackTrace();
        	context.getLogger().log("Log:" + e.toString());
        }
        
        
    	/* **Check File Name** */
        context.getLogger().log(d_PROCESS_CHECK_FILE_NAME);
        
        if(!key.isEmpty()) {
        	if(key.startsWith(d_CSV_FILE_PREFIX)) {
				if(key.endsWith(d_CSV_FILE_SUFFIX)) {
					//key = csv/ 20180815101116 _deliInst.csv
					//key NEW ="csv/ deliInst_20180720101112333.csv"; //20181020
					//strData =subSequence( 4, 4+14)
					//strData NEW=subSequence( 13, 13+17) //20181020
					String strData = (String) key.subSequence(d_CSV_FILE_PREFIX_LENGTH,d_CSV_FILE_PREFIX_LENGTH+d_CSV_FILE_DATE_LEN);
					
					strFileName =(String) key.substring(d_CSV_FILE_PREFIX_LENGTH);
					System.out.println("S3FileName:"+strFileName);	
					System.out.println(strData);	
					
					if(strData.length()==d_CSV_FILE_DATE_LEN) {
						try {
							
							SimpleDateFormat sdfCSVDate = new SimpleDateFormat(d_CSV_FILE_DATE_FORMAT);
							sdfCSVDate.setLenient(false);
							String Data =strData.subSequence(0,4)+
									"-"+
									strData.subSequence(4,6)+
									"-"+
									strData.subSequence(6,8)+
									" "+
									strData.subSequence(8,10)+
									":"+
									strData.subSequence(10,12)+
									":"+
									strData.subSequence(12,14)+
									":"+
									strData.subSequence(14,17);
							//System.out.println(Data);
							
							sdfCSVDate.parse(Data);
							context.getLogger().log(d_PROCESS_CHECK_CORRECT);
							context.getLogger().log("FILE_NAME:"+key);
							context.getLogger().log(d_PROCESS_CHECK_END);
							
							bReturnCode = d_OK;
								
						  }catch (ParseException e) {
 									bReturnCode = d_DATE_FORMAT_ERROR;
 					     			context.getLogger().log(d_TAG_CHECK_FILENAME +"DATE_FORMAT_ERROR");

						  }
						
					}else {
						bReturnCode = d_FILE_DATE_LEN_ERROR;
						context.getLogger().log(d_TAG_CHECK_FILENAME +"FILE_DATE_LEN_ERROR");
					}
				}else {
					bReturnCode = d_FILE_SUFFIX_ERROR;
					context.getLogger().log(d_TAG_CHECK_FILENAME +"FILE_SUFFIX_ERROR");

				}
        	}else {
        		bReturnCode = d_FILE_PREFIX_ERROR;
				context.getLogger().log(d_TAG_CHECK_FILENAME +"FILE_PREFIX_ERROR");
        		
        	}
		}else {
			bReturnCode = d_FILE_LEN_ERROR;
			context.getLogger().log(d_TAG_CHECK_FILENAME +"d_FILE_LEN_ERROR");
		}
     
        /* **Connect to http server  ** */
        
        if(bReturnCode ==d_OK) {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
  	        strCSVConText = s3Client.getObjectAsString(bucket, key)
 	        		.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1")
 	        		.replaceAll("^((\r\n)|\n)", "").trim();
        	
        	
        	RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("CMD",
                    		"{\"Authority\":{\"UserName\":"+"\""+d_POST_BODY_USERNAME+"\","
                    				+ "\"Password\":\""+d_POST_BODY_PASSWORD+"\"},"
                    						+ "\"FuncName\":\""+d_POST_BODY_FUNCTION_NAME+"\","
                    								+ "\"CreateMode\":"+d_POST_BODY_CREATEMODE+"}")
                    .addFormDataPart("Upload", strFileName, RequestBody.create(MediaType.parse("text/csv"),
                    		strCSVConText))
                    .build();

            context.getLogger().log(d_PROCESS_POST);

        	
        	Call<String> call = null;
            call = RetrofitClient.httpCSV().postCSV(requestBody);

            try {
            	Response<String> response = call.execute();
            	System.out.print("My response : \r\n"+response.body().toString()+"\r\n");
            
     
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

           
        }else {
			context.getLogger().log(d_STATUS+ bReturnCode);
		}
        
  
    	context.getLogger().log("lambda end!");

        return "lambda end!";
    }
  
}