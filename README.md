# Preface
----------
This project contain the aws lambda function and it can trigger when some event is happened.
Also,there are extra shell for user to auto generate Jar file which AWS Lambda need you to upload it. 

**This demo also improve the input effective by auto post instead of manual.** 

# Structure
----------
photo 

* This demo function following above photo step from 1 to 3.
  * Step1. Some event happened and Lambda was triggered.
  * Step2. Lambda function was working and do something as your code.
  * Step3. Lambda function will connect to server that you assigned.

* The **PUT** means some people upload the file to S3 bucket.

# Library
----------
  * [AWS Lambda API.](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
  * Connect to server with [okhttp3](https://square.github.io/okhttp/) and [retrofit2](https://square.github.io/retrofit/).
  * [Gradle](https://gradle.org/).
  * Linux shell or dos Script.

# Features
----------
  * Use AWS Lambda API to get event,for example :
    * Implement the **RequestHandler** and override **handleRequest**.
        ```java
        public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

        @Override
        public String handleRequest(S3Event event, Context context) {
        ...
        }
        ```
    * Get the **bucket** and **key** from event.
        ```java
        bucket = event.getRecords().get(0).getS3().getBucket().getName();
        key = event.getRecords().get(0).getS3().getObject().getKey();
        ```
    * And do something you want through **bucket**  and **key** .
        ```java
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        strCSVConText = s3Client.getObjectAsString(bucket, key)
        ```

  * Support https connect with okhttp and retrofit.
    * Easy way to add new Web API, you can add some code as same as below into AppAPI.java file :
        ```java
        $ @POST("/api/api_delivery.php")
        Call<String> postCSV(@Body RequestBody requestBody);
        ```

 * Support customize configuration.
    * There are constant file located ```../com.amazonaws.lambda.demo.constants``` ,
    you just need to set the value you want and you can make the post result in different way.
    E.g. Change the **HTTP_SERVER** Constant and the post result will be different.
        ```java
            public static final String HTTP_SERVER = "http://192.120.98.177:7088";
        ```
 * Support easy way to build jar file.
    * Windows 
     Just need to click the **Lambda_s3_makeJar.bat** file in this project and jar file will auto build without others step.
    * Mac 
     You should open the Terminal from MacOs and type the order ```./Lambda_s3_makeJar.sh```.

# Hints
----------
 * Use bolow code to show log from AWS Cloudwatch.
     ```
     context.getLogger().log("somthing...");
     ```
    (The object **context** here is from AWS override function.)
    
 * How to upload your AWS Lambda function.
   


