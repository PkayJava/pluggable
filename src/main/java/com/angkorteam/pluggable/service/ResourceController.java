package com.angkorteam.pluggable.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javaxt.io.Image;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.angkorteam.pluggable.PluggableConstants;
import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.doc.ApiMethod;
import com.angkorteam.pluggable.doc.ApiParam;
import com.angkorteam.pluggable.rest.Controller;
import com.angkorteam.pluggable.rest.RequestMapping;
import com.angkorteam.pluggable.rest.RequestMethod;
import com.angkorteam.pluggable.rest.Result;

@Controller
public class ResourceController {

    public static final String FILE = "/file";

    public static final String IMAGE = "/image";

    @ApiMethod(description = "file download", responseObject = byte[].class)
    @RequestMapping(value = FILE, method = RequestMethod.GET)
    public Result file(AbstractWebApplication application,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String filename = request.getParameter("filename");
        String type = request.getParameter("type");
        if (type == null || type.equals("")) {
            type = application.select(PluggableConstants.REPOSITORY,
                    String.class);
        } else {
            if (!type.equals(PluggableConstants.REPOSITORY_AMAZON_S3)
                    || !type.equals(PluggableConstants.REPOSITORY_LOCAL)) {
                type = application.select(PluggableConstants.REPOSITORY,
                        String.class);
            }
        }

        File cache = new File(FileUtils.getTempDirectory(), filename);
        File file = null;
        if (cache.exists()) {
            file = cache;
        } else {
            if (PluggableConstants.REPOSITORY_LOCAL.equals(type)) {
                String repository = application.select(
                        PluggableConstants.LOCAL, String.class);
                File local = new File(repository, filename);
                FileUtils.copyFile(local, cache);
                file = cache;
            } else if (PluggableConstants.REPOSITORY_AMAZON_S3.equals(type)) {
                AmazonS3 client = null;
                String bucketName = null;
                String accessKey = application.select(
                        PluggableConstants.AWS_S3_ACCESS_KEY, String.class);
                String secretKey = application.select(
                        PluggableConstants.AWS_S3_SECRET_KEY, String.class);
                bucketName = application.select(
                        PluggableConstants.AWS_S3_BUCKET_NAME, String.class);
                BasicAWSCredentials credentials = new BasicAWSCredentials(
                        accessKey, secretKey);
                client = new AmazonS3Client(credentials);
                S3Object object = client.getObject(bucketName, filename);
                InputStream input = object.getObjectContent();
                OutputStream output = new FileOutputStream(cache);
                IOUtils.copy(input, output);
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
                file = cache;
            }
        }
        return Result.ok(file, request, response);
    }

    @ApiMethod(description = "image download", requestParameters = {
            @ApiParam(name = "ratio", description = "keep ratio", type = Boolean.class),
            @ApiParam(name = "type", type = String.class, allowedvalues = {
                    PluggableConstants.REPOSITORY_LOCAL,
                    PluggableConstants.REPOSITORY_AMAZON_S3 }) }, responseObject = byte[].class)
    @RequestMapping(value = IMAGE, method = RequestMethod.GET)
    public Result image(AbstractWebApplication application,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String filename = request.getParameter("filename");
        String type = request.getParameter("type");
        if (type == null || type.equals("")) {
            type = application.select(PluggableConstants.REPOSITORY,
                    String.class);
        } else {
            if (!type.equals(PluggableConstants.REPOSITORY_AMAZON_S3)
                    && !type.equals(PluggableConstants.REPOSITORY_LOCAL)) {
                type = application.select(PluggableConstants.REPOSITORY,
                        String.class);
            }
        }

        boolean ratio = true;
        if (request.getParameter("ratio") != null
                && request.getParameter("ratio") != null) {
            ratio = Boolean.valueOf(request.getParameter("ratio"));
        }

        File cache = new File(FileUtils.getTempDirectory(), filename);
        File file = null;
        if (cache.exists()) {
            file = cache;
        } else {
            String extension = FilenameUtils.getExtension(filename);
            String basename = FilenameUtils.getBaseName(filename);
            if (PluggableConstants.REPOSITORY_LOCAL.equals(type)) {
                String repository = application.select(
                        PluggableConstants.LOCAL, String.class);
                File local = new File(repository, filename);
                if (local.exists()) {
                    FileUtils.copyFile(local, cache);
                    file = cache;
                } else {
                    int i = basename.lastIndexOf('_');
                    String original = basename.substring(0, i);
                    int height = Integer.valueOf(basename.substring(i + 1));
                    i = original.lastIndexOf('_');
                    int width = Integer.valueOf(original.substring(i + 1));
                    original = original.substring(0, i) + "." + extension;
                    local = new File(repository, original);
                    if (local.exists()) {
                        Image image = new Image(local);
                        image.resize(width, height, ratio);
                        image.saveAs(cache);
                        file = cache;
                    }
                }
            } else if (PluggableConstants.REPOSITORY_AMAZON_S3.equals(type)) {
                AmazonS3 client = null;
                String bucketName = null;
                String accessKey = application.select(
                        PluggableConstants.AWS_S3_ACCESS_KEY, String.class);
                String secretKey = application.select(
                        PluggableConstants.AWS_S3_SECRET_KEY, String.class);
                bucketName = application.select(
                        PluggableConstants.AWS_S3_BUCKET_NAME, String.class);
                BasicAWSCredentials credentials = new BasicAWSCredentials(
                        accessKey, secretKey);
                client = new AmazonS3Client(credentials);
                S3Object object = client.getObject(bucketName, filename);
                if (object != null) {
                    InputStream input = object.getObjectContent();
                    OutputStream output = new FileOutputStream(cache);
                    IOUtils.copy(input, output);
                    IOUtils.closeQuietly(input);
                    IOUtils.closeQuietly(output);
                    file = cache;
                } else {
                    int i = basename.lastIndexOf('_');
                    String original = basename.substring(0, i);
                    int height = Integer.valueOf(basename.substring(i + 1));
                    i = original.lastIndexOf('_');
                    int width = Integer.valueOf(original.substring(i + 1));
                    original = original.substring(0, i) + "." + extension;
                    object = client.getObject(bucketName, original);
                    if (object != null) {
                        InputStream input = object.getObjectContent();
                        OutputStream output = new FileOutputStream(cache);
                        IOUtils.copy(input, output);
                        IOUtils.closeQuietly(input);
                        IOUtils.closeQuietly(output);
                        Image image = new Image(cache);
                        image.resize(width, height, ratio);
                        image.saveAs(cache);
                        file = cache;
                    }
                }
            }
        }
        return Result.ok(file, request, response);
    }

}