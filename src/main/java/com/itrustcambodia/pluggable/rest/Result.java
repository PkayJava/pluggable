package com.itrustcambodia.pluggable.rest;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.itrustcambodia.pluggable.json.HttpMessage;

/**
 * @author Socheat KHAUV
 */
public class Result implements Serializable {

    private static final int DEFAULT_BUFFER_SIZE = 10240;

    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

    /**
     * 
     */
    private static final long serialVersionUID = -1733741221268184786L;

    private int status;

    private String contentType;

    private Result() {
    }

    private Result(HttpServletResponse response, int status) {
        this.status = status;
        response.setStatus(status);
    }

    private Result(HttpServletResponse response, int status, String contentType) {
        this.status = status;
        this.contentType = contentType;
        response.setStatus(status);
        response.setContentType(contentType);
    }

    public static final Result ok(HttpServletResponse response) {
        Result result = new Result(response, 200);
        return result;
    }

    public static final Result fake() {
        return new Result();
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

    private static class Range {
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

    public static final Result ok(File file, HttpServletRequest request,
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
            return Result.fake();
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
            return Result.fake();
        }

        // Validate request headers for resume
        // ----------------------------------------------------

        // If-Match header should contain "*" or ETag. If not, then
        // return 412.
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !matches(ifMatch, eTag)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return Result.fake();
        }

        // If-Unmodified-Since header should be greater than
        // LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return Result.fake();
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
                return Result.fake();
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
                        return Result.fake();
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
        return Result.fake();
    }

    public static final Result ok(HttpServletResponse response,
            String contentType) {
        Result result = new Result(response, 200, contentType);
        return result;
    }

    public static final Result ok(HttpServletResponse response, Gson gson,
            HttpMessage<?> content) throws JsonIOException, IOException {
        Result result = new Result(response, 200, "application/json");
        gson.toJson(content, response.getWriter());
        return result;
    }

    public static final Result created(HttpServletResponse response) {
        return new Result(response, 201);
    }

    public static final Result created(HttpServletResponse response,
            String contentType) {
        return new Result(response, 201, contentType);
    }

    public static final Result accepted(HttpServletResponse response) {
        return new Result(response, 202);
    }

    public static final Result accepted(HttpServletResponse response,
            String contentType) {
        return new Result(response, 202, contentType);
    }

    public static final Result found(HttpServletResponse response) {
        return new Result(response, 302);
    }

    public static final Result found(HttpServletResponse response,
            String contentType) {
        return new Result(response, 302, contentType);
    }

    public static final Result badRequest(HttpServletResponse response) {
        return new Result(response, 400);
    }

    public static final Result badRequest(HttpServletResponse response,
            String contentType) {
        return new Result(response, 400, contentType);
    }

    public static final Result unauthorized(HttpServletResponse response) {
        return new Result(response, 401);
    }

    public static final Result unauthorized(HttpServletResponse response,
            String contentType) {
        return new Result(response, 401, contentType);
    }

    public static final Result forbidden(HttpServletResponse response) {
        return new Result(response, 403);
    }

    public static final Result forbidden(HttpServletResponse response,
            String contentType) {
        return new Result(response, 403, contentType);
    }

    public static final Result notFound(HttpServletResponse response) {
        return new Result(response, 404);
    }

    public static final Result notFound(HttpServletResponse response,
            String contentType) {
        return new Result(response, 404, contentType);
    }

    public static final Result gone(HttpServletResponse response) {
        return new Result(response, 410);
    }

    public static final Result gone(HttpServletResponse response,
            String contentType) {
        return new Result(response, 410, contentType);
    }

    public static final Result locked(HttpServletResponse response) {
        return new Result(response, 423);
    }

    public static final Result locked(HttpServletResponse response,
            String contentType) {
        return new Result(response, 423, contentType);
    }

    public static final Result internalServerError(HttpServletResponse response) {
        return new Result(response, 500);
    }

    public static final Result internalServerError(
            HttpServletResponse response, String contentType) {
        return new Result(response, 500, contentType);
    }

    public static final Result notImplemented(HttpServletResponse response) {
        return new Result(response, 501);
    }

    public static final Result notImplemented(HttpServletResponse response,
            String contentType) {
        return new Result(response, 501, contentType);
    }

    public static final Result serviceUnavailable(HttpServletResponse response) {
        return new Result(response, 503);
    }

    public static final Result serviceUnavailable(HttpServletResponse response,
            String contentType) {
        return new Result(response, 503, contentType);
    }

    public int getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

}
