package com.itrustcambodia.pluggable;

import java.util.Arrays;
import java.util.List;

public interface PluggableConstants {

    String REPOSITORY = "repository";
    String REPOSITORY_LOCAL = "Local";
    String REPOSITORY_AMAZON_S3 = "Amazon S3";
    List<String> REPOSITORY_CHIOCE = Arrays.asList(REPOSITORY_LOCAL, REPOSITORY_AMAZON_S3);

    String REPOSITORY_LOCAL_URI = "file://";
    String REPOSITORY_AMAZON_S3_URI = "aw_s3://";

    String LOCAL = "local";

    String X_FORWARDED_FOR = "x-forwarded-for";

    String AWS_S3_ACCESS_KEY = "aws_s3_access_key";
    String AWS_S3_SECRET_KEY = "aws_s3_secret_key";
    String AWS_S3_BUCKET_NAME = "aws_s3_bucket_name";
    String AWS_S3_BUCKET_PATH = "aws_s3_bucket_path";

    String SERVER_ADDRESS = "server_address";

    String DEBUG = "debug";

    String DEPRECATED = "deprecated";

}
