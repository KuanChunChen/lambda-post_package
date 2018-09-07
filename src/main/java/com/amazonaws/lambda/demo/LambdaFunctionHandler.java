package com.amazonaws.lambda.demo;


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

	/* **define value ** */
	/* *Check file name* */
	//private static final int d_CSV_FILE_NAME_LEN = 34;
	private static final int d_CSV_FILE_DATE_LEN = 14;
	private static final int d_CSV_FILE_SUFFIX_LENGTH = 4;
	private static final int d_CSV_FILE_PREFIX_LENGTH = 4;
	private static final String d_CSV_FILE_PREFIX = "csv/";
	private static final String d_CSV_FILE_SUFFIX = ".csv";
	private static final String d_CSV_FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String d_TARGET_CONN_URL = "http://218.211.35.215:8080/api/api_delivery.php";
	
	/* *Error Code* */
	private static final byte d_OK = 0x00;
	private static final byte d_DATE_FORMAT_ERROR = 0x01;
	private static final byte d_FILE_LEN_ERROR = 0x02;
	private static final byte d_FILE_PREFIX_ERROR = 0x03;
	private static final byte d_FILE_SUFFIX_ERROR = 0x04;
	private static final byte d_FILE_DATE_LEN_ERROR = 0x05;
	
	


	/* *SOMETHING* */
	private static String d_LINE_FEED = "\r\n";
	//private static String d_FONT ="UTF-8";

	/* *POST Define* */
	
	private static  final String d_POST_HEADER_CONNECT = "keep-alive";
	private static  final String d_POST_HEADER_CONTENT_TYPE = "multipart/form-data;";
	private static  final String d_POST_HEADER_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
	private static  final String d_POST_HEADER_ACCEPT_LANGUAGE = "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7";

	private static  final String d_POST_BODY_CONTENT_TYPE = "application/octet-stream";
	private static  final String d_POST_BODY_FILES_PARM = "Upload";
	private static  final String d_POST_BODY_POST_PARM = "CMD";
	private static  final String d_POST_BODY_USERNAME = "admin";
	private static  final String d_POST_BODY_PASSWORD = "castles8418";
	private static  final String d_POST_BODY_FUNCTION_NAME = "CREATEDELIVERY";
	private static  final String d_POST_BODY_CREATEMODE = "2";

	private static  final String d_CONN_URL_REQUEST_METHOD = "POST";

	private static  final String d_POST_BOUNDARY = "----WebKitFormBoundary"+Long.toHexString(System.currentTimeMillis());
	
	/* **Process ** */
	 
	private static  final String d_TAG_CHECK_FILENAME = "CHECK FILE NAME：";
	private static  final String d_PROCESS_CHECK_FILE_NAME = "------Strat Checking file name------";
	private static  final String d_PROCESS_CHECK_END = "------Check File Name End------";
	private static  final String d_PROCESS_CHECK_CORRECT = "---File Name Correct---";
	private static  final String d_PROCESS_POST = "------Start Post to URL------";
	private static  final String d_POST_STATUS = "HTTP POST Status:";

	
	 
	private static String d_STATUS = "Return Code:";
	/* **handle ** */
	/* **lambda process beginning point ** */
    @Override
    public String handleRequest(S3Event event, Context context) {
    	
    	byte bReturnCode;
    	
        //context.getLogger().log("Received event: " + event);
        //context.getLogger().log("Event Detail: " + event.toJson());
        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        String strFileName=null;
        
        
    	/* **Check File Name** */
        context.getLogger().log(d_PROCESS_CHECK_FILE_NAME);
        
        if(!key.isEmpty()) {
        	if(key.startsWith(d_CSV_FILE_PREFIX)) {
				if(key.endsWith(d_CSV_FILE_SUFFIX)) {
					String strData = (String) key.subSequence(d_CSV_FILE_PREFIX_LENGTH,d_CSV_FILE_PREFIX_LENGTH+d_CSV_FILE_DATE_LEN);
					strFileName =(String) key.substring(d_CSV_FILE_PREFIX_LENGTH);
					//System.out.println("S3FileName:"+strFileName);	
					//System.out.println(strData);	
					
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
									strData.subSequence(12,14);
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
        	
        	
        
        	
      
        	context.getLogger().log(d_PROCESS_POST);
        	try{
            	 
        		int iRespondCode;
        		StringBuilder postData = new StringBuilder();
     			HttpURLConnection connection = null;
     			//String strBoundary = "----WebKitFormBoundary"+Long.toHexString(System.currentTimeMillis());
     	        URL url = new URL(d_TARGET_CONN_URL);
     	        
     	        /* *Get Data from S3* */
     	        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
     	        //S3Object object = s3Client.getObject(new GetObjectRequest(bucket,key));
     	        //InputStream objectData = object.getObjectContent();
     	        String strCSVConText = s3Client.getObjectAsString(bucket, key);
     	        
     	        
     			connection = (HttpURLConnection)  url.openConnection();
     			
     			
     			
     			/* *package header * */
     			connection.setRequestMethod(d_CONN_URL_REQUEST_METHOD);
     			connection.setDoOutput(true);
     			connection.setDoInput(true);
     			connection.setUseCaches(false); 
     			connection.setRequestProperty("Connection", d_POST_HEADER_CONNECT);
     			connection.setRequestProperty("Content-Type",d_POST_HEADER_CONTENT_TYPE +" boundary="+d_POST_BOUNDARY);
     			connection.setRequestProperty("Accept",d_POST_HEADER_ACCEPT);
     			connection.setRequestProperty("Accept-Language",d_POST_HEADER_ACCEPT_LANGUAGE); 
     					   
     			
     			/* *package body * */
     			postData.append("--" + d_POST_BOUNDARY).append(d_LINE_FEED);
     		    postData.append("Content-Disposition: form-data;" + " name="+"\""+d_POST_BODY_FILES_PARM +"\"; filename=\"" + strFileName+ "\"").append(d_LINE_FEED);
     		    postData.append("Content-Type:"+d_POST_BODY_CONTENT_TYPE);
     		    postData.append(d_LINE_FEED);
     		    postData.append(d_LINE_FEED);
     		    postData.append(strCSVConText).append(d_LINE_FEED);;
     		    postData.append("--" + d_POST_BOUNDARY).append(d_LINE_FEED);
     		    postData.append("Content-Disposition: form-data;"+"name="+d_POST_BODY_POST_PARM).append(d_LINE_FEED).append(d_LINE_FEED);
     		    postData.append("{\"Authority\":{\"UserName\":"+"\""+d_POST_BODY_USERNAME+"\",\"Password\":\""+d_POST_BODY_PASSWORD+"\"},\"FuncName\":\""+d_POST_BODY_FUNCTION_NAME+"\",\"CreateMode\":"+d_POST_BODY_CREATEMODE+"}");
     		   
    		    byte[] postDataBytes = postData.toString().getBytes("UTF-8");
    		    connection.getOutputStream().write(postDataBytes);
    		    
    		    
    		    iRespondCode = connection.getResponseCode();
        		if(iRespondCode==200) {
        			context.getLogger().log(d_POST_STATUS + iRespondCode);
        			StringBuffer strBuf = new StringBuffer();  
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));  
                    String line = null;  
                    while ((line = reader.readLine()) != null) {  
                        strBuf.append(line).append("\n");  
                    }  
                    
                    System.out.println("POST Responed:"+strBuf);
        		}else {
        			context.getLogger().log(d_POST_STATUS + iRespondCode);
            	}
        			 
    		}catch (MalformedURLException e1) {
    			context.getLogger().log("------MalformedURLException------");
    			e1.printStackTrace();
    			context.getLogger().log("------END------");
    		}catch (IOException e2) {
    			context.getLogger().log("------IOException------");
    			e2.printStackTrace();
    			context.getLogger().log("------END------");

    		}catch (Exception  e3) {
     			context.getLogger().log("------Exception------");
     			e3.printStackTrace();
    			context.getLogger().log("------END------");

     		}
             
        }else {
			context.getLogger().log(d_STATUS+ bReturnCode);
		}
        
        return "lambda end!";
    }
}