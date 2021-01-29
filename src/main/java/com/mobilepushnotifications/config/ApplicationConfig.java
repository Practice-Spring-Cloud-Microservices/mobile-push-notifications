package com.mobilepushnotifications.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${aws.sns.region}")
    private String awsSnsRegion;

    @Value("${aws.sns.accessKey}")
    private String awsSnsAccessKey;

    @Value("${aws.sns.secretKey}")
    private String awsSnsSecretKey;

    @Bean
    public AmazonSNS amazonSns() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsSnsAccessKey, awsSnsSecretKey);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return AmazonSNSClientBuilder
                .standard()
                .withRegion(awsSnsRegion)
                .withCredentials(awsCredentialsProvider)
                .build();
    }
}
