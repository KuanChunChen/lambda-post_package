package com.amazonaws.lambda.demo;


/**
 * Created by Willy Chen on 2019/07/10.
 * @feature Use this lambda function to upload csv to server.
 * @author Willy Chen
 * @version v1.0.4
 */



import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.amazonaws.lambda.demo.http.RetrofitClient;
import com.amazonaws.lambda.demo.util.ParserUtil;
import com.amazonaws.lambda.demo.util.ConnectUtil;
import com.amazonaws.lambda.demo.constants.Constants;
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





public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {


	private String strCSVConText;
	private String strFileName;

	/* **handle ** */
	/* **lambda process beginning point ** */
	@Override
	public String handleRequest(S3Event event, Context context) {

		byte bReturnCode;	 
		String bucket = null;
		String key = null;


		try {
			bucket = event.getRecords().get(0).getS3().getBucket().getName();
			key = event.getRecords().get(0).getS3().getObject().getKey();
			strFileName = null;


			/* **Check File Name** */
			context.getLogger().log(Constants.Message.PROCESS_CHECK_FILE_NAME);
			bReturnCode = ParserUtil.isFileNamePass(key,strFileName);

			/* **Connect to http server  ** */

			if(bReturnCode == Constants.ReturnCode.OK) {

				ConnectUtil.sentDelivery(bucket,key,strFileName,strCSVConText);

			}else {
				context.getLogger().log("Return Code : " + bReturnCode);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
			context.getLogger().log(" Exception :" + e.toString());
		}





		context.getLogger().log("lambda end!");

		return "lambda end!";
	}

}
