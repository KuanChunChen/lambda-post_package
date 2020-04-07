package com.amazonaws.lambda.demo.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.amazonaws.lambda.demo.constants.Constants;



public class ParserUtil{


	public static byte isFileNamePass(String key,String strFileName){


		if(key.isEmpty()) {
			return Constants.ReturnCode.FILE_LEN_ERROR;
		}


		if(!key.startsWith(Constants.File.CSV_FILE_PREFIX)) {
			return Constants.ReturnCode.FILE_PREFIX_ERROR;
		}

		if(!key.endsWith(Constants.File.CSV_FILE_SUFFIX)) {
			return Constants.ReturnCode.FILE_SUFFIX_ERROR;
		}


		//key = csv/ 20180815101116 _deliInst.csv
		//key NEW ="csv/ deliInst_20180720101112333.csv"; //20181020
		//strData =subSequence( 4, 4+14)
		//strData NEW=subSequence( 13, 13+17) //20181020

		String strData = (String) key.subSequence(Constants.File.CSV_FILE_PREFIX_LENGTH,
			Constants.File.CSV_FILE_PREFIX_LENGTH + Constants.File.CSV_FILE_DATE_LEN);
		strFileName = (String) key.substring(Constants.File.CSV_FILE_PREFIX_LENGTH);
		System.out.println("S3FileName:" + strFileName);	
		System.out.println(strData);	


		if(strData.length() != Constants.File.CSV_FILE_DATE_LEN)  {
			return Constants.ReturnCode.FILE_DATE_LEN_ERROR;
		}



		try {

			SimpleDateFormat sdfCSVDate = new SimpleDateFormat(Constants.File.CSV_FILE_DATE_FORMAT);
			sdfCSVDate.setLenient(false);
			String Data = strData.subSequence(0,4)+
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

			sdfCSVDate.parse(Data);
							

			return Constants.ReturnCode.OK;

		}catch (ParseException e) {

			return Constants.ReturnCode.DATE_FORMAT_ERROR;

		}
	}

}