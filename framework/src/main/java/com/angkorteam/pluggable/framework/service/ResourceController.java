package com.angkorteam.pluggable.framework.service;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;

import com.angkorteam.pluggable.framework.FrameworkConstants;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.doc.ApiMethod;
import com.angkorteam.pluggable.framework.doc.ApiParam;
import com.angkorteam.pluggable.framework.rest.Controller;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.rest.RequestMethod;
import com.angkorteam.pluggable.framework.rest.Result;

@Controller
public class ResourceController {

    public static final String FILE = "/file";

    public static final String IMAGE = "/image";

    @ApiMethod(description = "file download")
    @RequestMapping(value = FILE, method = RequestMethod.GET)
    public Result<byte[]> file(
            AbstractWebApplication application,
            WebRequest request,
            WebResponse response,
            @ApiParam(name = "filename", required = true, type = String.class) StringValue filename)
            throws IOException {

        if (filename.toOptionalString() == null
                || "".equals(filename.toOptionalString())) {
            return Result.badRequest(response, byte[].class);
        }
        System.out
                .println("===============================================================");
        System.out.println("filename " + filename);
        Enumeration<String> headers = ((HttpServletRequest) request
                .getContainerRequest()).getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            System.out.println(header + " : " + request.getHeader(header));
        }

        File cache = new File(FileUtils.getTempDirectory(),
                filename.toOptionalString());
        File file = null;
        if (cache.exists()) {
            file = cache;
        } else {
            String repository = application.select(
                    FrameworkConstants.REPOSITORY, String.class);
            File local = new File(repository, filename.toOptionalString());
            FileUtils.copyFile(local, cache);
            file = cache;
        }
        return Result.ok(application, file, response);
    }

    @ApiMethod(description = "image download")
    @RequestMapping(value = IMAGE, method = RequestMethod.GET)
    public Result<byte[]> image(
            AbstractWebApplication application,
            WebRequest request,
            WebResponse response,
            @ApiParam(name = "filename", required = true, type = String.class) StringValue filename,
            @ApiParam(name = "ratio", required = false, type = Boolean.class) StringValue ratio)
            throws IOException {
        if (filename.toOptionalString() == null
                || "".equals(filename.toOptionalString())) {
            return Result.badRequest(response, byte[].class);
        }

        File cache = new File(FileUtils.getTempDirectory(),
                filename.toOptionalString());
        File file = null;
        if (cache.exists()) {
            file = cache;
        } else {
            String extension = FilenameUtils.getExtension(filename
                    .toOptionalString());
            String basename = FilenameUtils.getBaseName(filename
                    .toOptionalString());
            String repository = application.select(
                    FrameworkConstants.REPOSITORY, String.class);
            File local = new File(repository, filename.toOptionalString());
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
                    BufferedImage bufferedImage = ImageIO.read(local);
                    bufferedImage = getScaledImage(bufferedImage, width, height);
                    ImageIO.write(
                            bufferedImage,
                            FilenameUtils.getExtension(local.getAbsolutePath()),
                            cache);
                    file = cache;
                }
            }
        }
        return Result.ok(application, file, response);
    }

    public static BufferedImage getScaledImage(BufferedImage image, int width,
            int height) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(
                scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(
                scaleTransform, AffineTransformOp.TYPE_BILINEAR);

        return bilinearScaleOp.filter(image, new BufferedImage(width, height,
                image.getType()));
    }

}