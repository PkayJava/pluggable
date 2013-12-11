package com.itrustcambodia.pluggable.service;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javaxt.io.Image;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.itrustcambodia.pluggable.PluggableConstants;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.doc.ApiMethod;
import com.itrustcambodia.pluggable.doc.ApiParam;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.rest.RequestMethod;
import com.itrustcambodia.pluggable.rest.Result;

@Controller
public class ResourceController {

    private static final int DEFAULT_BUFFER_SIZE = 10240;

    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

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
        stream(file, request, response);
        return Result.ok();
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
        stream(file, request, response);
        return Result.ok();
    }

    private void stream(File file, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        DateTime now = new DateTime();

        // Prepare some variables. The ETag is an unique identifier of
        // the file.
        String fileName = file.getName();
        long length = file.length();
        long lastModified = file.lastModified();
        String eTag = fileName + "_" + length + "_" + lastModified;

        // Validate request headers for caching
        // ---------------------------------------------------

        // If-None-Match header should contain "*" or ETag. If so, then
        // return 304.
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
            response.setHeader("ETag", eTag); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // If-Modified-Since header should be greater than LastModified.
        // If so, then return 304.
        // This header is ignored if any If-None-Match header is
        // specified.
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1
                && ifModifiedSince + 1000 > lastModified) {
            response.setHeader("ETag", eTag); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // Validate request headers for resume
        // ----------------------------------------------------

        // If-Match header should contain "*" or ETag. If not, then
        // return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !matches(ifMatch, eTag)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        // If-Unmodified-Since header should be greater than
        // LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return;
        }

        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<Range>();

        String range = request.getHeader("Range");
        if (range != null) {

            // Range header should match format "bytes=n-n,n-n,n-n...".
            // If not, then return 416.
            if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
                response.setHeader("Content-Range", "bytes */" + length);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            String ifRange = request.getHeader("If-Range");
            if (ifRange != null && !ifRange.equals(eTag)) {
                try {
                    long ifRangeTime = request.getDateHeader("If-Range");
                    if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
                        ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    ranges.add(full);
                }
            }

            if (ranges.isEmpty()) {
                for (String part : range.substring(6).split(",")) {
                    long start = sublong(part, 0, part.indexOf("-"));
                    long end = sublong(part, part.indexOf("-") + 1,
                            part.length());

                    if (start == -1) {
                        start = length - end;
                        end = length - 1;
                    } else if (end == -1 || end > length - 1) {
                        end = length - 1;
                    }

                    if (start > end) {
                        response.setHeader("Content-Range", "bytes */" + length);
                        response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                        return;
                    }

                    // Add range.
                    ranges.add(new Range(start, end, length));
                }
            }
        }

        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);

        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", eTag);
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader("Expires", now.plusMonths(1).getMillis());

        String extension = FilenameUtils.getExtension(file.getName())
                .toLowerCase();
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            response.setContentType("image/jpg");
            response.setHeader("Content-Type", "image/jpg");
        } else if (extension.equals("png")) {
            response.setContentType("image/png");
            response.setHeader("Content-Type", "image/png");
        } else if (extension.equals("gif")) {
            response.setContentType("image/gif");
            response.setHeader("Content-Type", "image/gif");
        }

        // response.setHeader("Content-Disposition", "attachment; filename=\"" +
        // file.getName() + "\"");

        RandomAccessFile input = null;
        OutputStream output = null;

        try {
            // Open streams.
            input = new RandomAccessFile(file, "r");
            output = response.getOutputStream();

            if (ranges.isEmpty() || ranges.get(0) == full) {

                // Return full file.
                Range r = full;
                response.setHeader("Content-Range", "bytes " + r.start + "-"
                        + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));

                // Copy full range.
                copy(input, output, r.start, r.length);

            } else if (ranges.size() == 1) {

                // Return single part of file.
                Range r = ranges.get(0);
                response.setHeader("Content-Range", "bytes " + r.start + "-"
                        + r.end + "/" + r.total);
                response.setHeader("Content-Length", String.valueOf(r.length));
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                copy(input, output, r.start, r.length);
            } else {

                // Return multiple parts of file.
                response.setContentType("multipart/byteranges; boundary="
                        + MULTIPART_BOUNDARY);
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

                // Cast back to ServletOutputStream to get the easy
                // println methods.
                ServletOutputStream sos = (ServletOutputStream) output;

                // Copy multi part range.
                for (Range r : ranges) {
                    // Add multipart boundary and header fields for
                    // every range.
                    sos.println();
                    sos.println("--" + MULTIPART_BOUNDARY);
                    sos.println("Content-Range: bytes " + r.start + "-" + r.end
                            + "/" + r.total);

                    // Copy single part range of multi part range.
                    copy(input, output, r.start, r.length);
                }

                // End with multipart boundary.
                sos.println();
                sos.println("--" + MULTIPART_BOUNDARY + "--");
            }
        } catch (EOFException e) {
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
        }
    }

    private static boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
                || Arrays.binarySearch(matchValues, "*") > -1;
    }

    private static long sublong(String value, int beginIndex, int endIndex) {
        String substring = value.substring(beginIndex, endIndex);
        return (substring.length() > 0) ? Long.parseLong(substring) : -1;
    }

    private static void copy(RandomAccessFile input, OutputStream output,
            long start, long length) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;

        if (input.length() == length) {
            // Write full range.
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } else {
            // Write partial range.
            input.seek(start);
            long toRead = length;

            while ((read = input.read(buffer)) > 0) {
                if ((toRead -= read) > 0) {
                    output.write(buffer, 0, read);
                } else {
                    output.write(buffer, 0, (int) toRead + read);
                    break;
                }
            }
        }
    }

    protected class Range {
        long start;
        long end;
        long length;
        long total;

        /**
         * Construct a byte range.
         * 
         * @param start
         *            Start of the byte range.
         * @param end
         *            End of the byte range.
         * @param total
         *            Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

    }
}