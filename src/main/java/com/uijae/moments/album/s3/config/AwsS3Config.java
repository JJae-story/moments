package com.uijae.moments.album.s3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

  @Value("${aws.credentials.access-key}")
  private String accessKey;

  @Value("${aws.credentials.secret-key}")
  private String secretKey;

  @Value("${aws.s3.region}")
  private String region;

  // 배포 하기 전 고정 -> 동적 인증 변경 예정
  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}
