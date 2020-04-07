package com.amazonaws.lambda.demo.constants;

public class Constants {


	public static final class Http {

		public static final String HTTP_SERVER = "http://192.120.98.177:7088";

	}

	public static final class ReturnCode {

		public static final byte OK = 0x00;
		public static final byte DATE_FORMAT_ERROR = 0x01;
		public static final byte FILE_LEN_ERROR = 0x02;
		public static final byte FILE_PREFIX_ERROR = 0x03;
		public static final byte FILE_SUFFIX_ERROR = 0x04;
		public static final byte FILE_DATE_LEN_ERROR = 0x05;
	}

	public static final class File {
	//public static final int d_CSV_FILE_NAME_LEN = 34;
		public static final int CSV_FILE_DATE_LEN = 17;
	//public static final int d_CSV_FILE_SUFFIX_LENGTH = 4;
		public static final int CSV_FILE_PREFIX_LENGTH = 18;
		public static final String CSV_FILE_PREFIX = "FromArch/deliInst_";
		public static final String CSV_FILE_SUFFIX = ".csv";
		public static final String CSV_FILE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
	}


	public static final class Package {

		private static final String POST_BODY_USERNAME = "test";
		private static final String POST_BODY_PASSWORD = "test1234";
		private static final String POST_BODY_FUNCTION_NAME = "CREATEDELIVERY";
		private static final String POST_BODY_CREATEMODE = "2";

		public static final String POST_REQUEST_BODY = "{\"Authority\":{\"UserName\":"+"\""+ POST_BODY_USERNAME +"\","
		+ "\"Password\":\""+POST_BODY_PASSWORD+"\"},"
		+ "\"FuncName\":\""+POST_BODY_FUNCTION_NAME+"\","
		+ "\"CreateMode\":"+POST_BODY_CREATEMODE+"}";

	}


	public static final class Message {

		public static final String PROCESS_CHECK_FILENAME = "CHECK FILE NAME：";
		public static final String PROCESS_CHECK_FILE_NAME = "------Strat Checking file name------";
		public static final String PROCESS_CHECK_END = "------Check File Name End------";
		public static final String PROCESS_CHECK_CORRECT = "---File Name Correct---";
		public static final String PROCESS_POST = "------Start Post to URL------";


	}

}
