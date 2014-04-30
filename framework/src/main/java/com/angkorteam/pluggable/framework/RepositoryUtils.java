package com.angkorteam.pluggable.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;

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

        String local = application.select(FrameworkConstants.REPOSITORY,
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

    public static String store(AbstractWebApplication application,
            FileUpload fileUpload) {
        return localStore(application, fileUpload);
    }

    private static void localDelete(AbstractWebApplication application,
            String filename) {
        String local = application.select(FrameworkConstants.REPOSITORY,
                String.class);
        File fileToDelete = new File(local, filename);
        FileUtils.deleteQuietly(fileToDelete);
    }

    public static void delete(AbstractWebApplication application, String file) {
        String filename = file
                .substring(FrameworkConstants.REPOSITORY_LOCAL_URI.length());
        localDelete(application, filename);
    }

    public static String getFileAccessLink(AbstractWebApplication application,
            String file) {
        String serverAddress = application.select(
                FrameworkConstants.SERVER_ADDRESS, String.class);

        return serverAddress
                + "/"
                + RestController.PATH
                + ResourceController.FILE
                + "?filename="
                + file.substring(FrameworkConstants.REPOSITORY_LOCAL_URI
                        .length());
    }

    public static String getAbsolutePath(AbstractWebApplication application,
            String file) {
        String local = application.select(FrameworkConstants.REPOSITORY,
                String.class);
        File fn = new File(
                local,
                file.substring(FrameworkConstants.REPOSITORY_LOCAL_URI.length()));
        return fn.getAbsolutePath();
    }

    public static String getImageAccessLink(AbstractWebApplication application,
            String file, int width, int height) {
        String serverAddress = application.select(
                FrameworkConstants.SERVER_ADDRESS, String.class);

        String filename = file
                .substring(FrameworkConstants.REPOSITORY_LOCAL_URI.length());
        String extension = FilenameUtils.getExtension(filename);
        String basename = FilenameUtils.getBaseName(filename);
        return serverAddress + "/" + RestController.PATH
                + ResourceController.IMAGE + "?filename=" + basename + "_"
                + width + "_" + height + "." + extension;
    }
}
