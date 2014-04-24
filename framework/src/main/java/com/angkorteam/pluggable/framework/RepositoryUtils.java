package com.angkorteam.pluggable.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.service.ResourceController;
import com.angkorteam.pluggable.framework.wicket.RestController;

//import com.angkorteam.pluggable.framework.framework.service.ResourceController;

public final class RepositoryUtils {

    private RepositoryUtils() {
    }

    private static String localStore(AbstractWebApplication application,
            FileUpload fileUpload) {
        String system = String.valueOf(System.nanoTime());
        String filename = org.apache.commons.codec.digest.DigestUtils
                .md5Hex(system) + "_" + fileUpload.getClientFileName();

        String local = application.select(FrameworkConstants.LOCAL,
                String.class);
        File copyTo = new File(local, filename);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(copyTo);
        } catch (IOException e) {
            return null;
        }
        if (out != null) {
            try {
                IOUtils.copy(fileUpload.getInputStream(), out);
            } catch (IOException e) {
                return null;
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
        return FrameworkConstants.REPOSITORY_LOCAL_URI + filename;
    }

    private static String amazonStore(AbstractWebApplication application,
            FileUpload fileUpload) {

        String system = String.valueOf(System.nanoTime());
        String filename = org.apache.commons.codec.digest.DigestUtils
                .md5Hex(system) + "_" + fileUpload.getClientFileName();

        AmazonS3 client = null;
        String bucketName = null;

        String accessKey = application.select(
                FrameworkConstants.AWS_S3_ACCESS_KEY, String.class);
        String secretKey = application.select(
                FrameworkConstants.AWS_S3_SECRET_KEY, String.class);
        bucketName = application.select(FrameworkConstants.AWS_S3_BUCKET_NAME,
                String.class);
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,
                secretKey);
        client = new AmazonS3Client(credentials);

        File copyTo = new File(FileUtils.getTempDirectoryPath(), filename);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(copyTo);
        } catch (IOException e) {
            return null;
        }
        if (out != null) {
            try {
                IOUtils.copy(fileUpload.getInputStream(), out);
            } catch (IOException e) {

            }
            IOUtils.closeQuietly(out);
        }

        try {
            client.putObject(bucketName, filename, copyTo);
        } catch (AmazonClientException amazon) {
            return null;
        }

        return FrameworkConstants.REPOSITORY_AMAZON_S3_URI + filename;

    }

    public static String store(AbstractWebApplication application,
            FileUpload fileUpload) {
        String repository = application.select(FrameworkConstants.REPOSITORY,
                String.class);
        if (FrameworkConstants.REPOSITORY_LOCAL.equals(repository)) {
            return localStore(application, fileUpload);
        } else if (FrameworkConstants.REPOSITORY_AMAZON_S3.equals(repository)) {
            return amazonStore(application, fileUpload);
        }
        return null;
    }

    private static void localDelete(AbstractWebApplication application,
            String filename) {
        String local = application.select(FrameworkConstants.LOCAL,
                String.class);
        File fileToDelete = new File(local, filename);
        FileUtils.deleteQuietly(fileToDelete);
    }

    private static void amazonDelete(AbstractWebApplication application,
            String filename) {
        AmazonS3 client = null;
        String bucketName = null;
        String accessKey = application.select(
                FrameworkConstants.AWS_S3_ACCESS_KEY, String.class);
        String secretKey = application.select(
                FrameworkConstants.AWS_S3_SECRET_KEY, String.class);
        bucketName = application.select(FrameworkConstants.AWS_S3_BUCKET_NAME,
                String.class);
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey,
                secretKey);
        client = new AmazonS3Client(credentials);
        try {
            client.deleteObject(bucketName, filename);
        } catch (AmazonClientException e) {
        }
    }

    public static void delete(AbstractWebApplication application, String file) {
        if (file.startsWith(FrameworkConstants.REPOSITORY_AMAZON_S3_URI)) {
            String filename = file
                    .substring(FrameworkConstants.REPOSITORY_AMAZON_S3_URI
                            .length());
            amazonDelete(application, filename);
        } else if (file.startsWith(FrameworkConstants.REPOSITORY_LOCAL_URI)) {
            String filename = file
                    .substring(FrameworkConstants.REPOSITORY_LOCAL_URI.length());
            localDelete(application, filename);
        }
    }

    public static String getFileAccessLink(AbstractWebApplication application,
            String file) {
        String serverAddress = application.select(
                FrameworkConstants.SERVER_ADDRESS, String.class);
        if (file.startsWith(FrameworkConstants.REPOSITORY_AMAZON_S3_URI)) {
            // return serverAddress + "/rest" + ResourceController.FILE + "/" +
            // file.substring(FrameworkConstants.REPOSITORY_AMAZON_S3_URI.length())
            // + "?type=" + FrameworkConstants.REPOSITORY_AMAZON_S3;
            return "";
        } else if (file.startsWith(FrameworkConstants.REPOSITORY_LOCAL_URI)) {
            return serverAddress
                    + "/"
                    + RestController.PATH
                    + "/"
                    + ResourceController.FILE
                    + "?filename="
                    + file.substring(FrameworkConstants.REPOSITORY_LOCAL_URI
                            .length()) + "&type="
                    + FrameworkConstants.REPOSITORY_LOCAL;
        }
        return null;
    }

    public static String getImageAccessLink(AbstractWebApplication application,
            String file, int width, int height) {
        String serverAddress = application.select(
                FrameworkConstants.SERVER_ADDRESS, String.class);
        if (file.startsWith(FrameworkConstants.REPOSITORY_AMAZON_S3_URI)) {
            String filename = file
                    .substring(FrameworkConstants.REPOSITORY_AMAZON_S3_URI
                            .length());
            String extension = FilenameUtils.getExtension(filename);
            String basename = FilenameUtils.getBaseName(filename);
            // return serverAddress + "/rest" + ResourceController.IMAGE + "/" +
            // basename + "_" + width + "_" + height + "." + extension +
            // "?type=" + FrameworkConstants.REPOSITORY_AMAZON_S3;
            return "";
        } else if (file.startsWith(FrameworkConstants.REPOSITORY_LOCAL_URI)) {
            String filename = file
                    .substring(FrameworkConstants.REPOSITORY_LOCAL_URI.length());
            String extension = FilenameUtils.getExtension(filename);
            String basename = FilenameUtils.getBaseName(filename);
            return serverAddress + "/" + RestController.PATH
                    + ResourceController.IMAGE + "?filename=" + basename + "_"
                    + width + "_" + height + "." + extension + "&type="
                    + FrameworkConstants.REPOSITORY_LOCAL;
        }
        return null;
    }
}
