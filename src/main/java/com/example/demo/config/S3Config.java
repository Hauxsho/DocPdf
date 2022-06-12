package com.example.demo.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${access-key}")
    private String accessKey;

    @Value("${secret-key}")
    private String seccretKey;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${region}")
    private String region;




    @Bean
    public AmazonS3 s3()
    {

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey ,seccretKey);

        return AmazonS3ClientBuilder.standard().withRegion(region).withCredentials( new AWSStaticCredentialsProvider(awsCredentials)).build();

    }

}
