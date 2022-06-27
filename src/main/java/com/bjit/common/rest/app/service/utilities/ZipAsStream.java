/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author BJIT
 */
public class ZipAsStream {
    private static final org.apache.log4j.Logger ZIP_AS_STREAM_LOGGER = org.apache.log4j.Logger.getLogger(ZipAsStream.class);

    public static ByteArrayOutputStream zipAsStream(List<String> directoryOrFileList) {
        String s = "hello world";

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream)) {

            /* File is not on the disk, test.txt indicates
               only the file name to be put into the zip */
            directoryOrFileList.forEach(fileOrDirectory -> {
                ZipEntry entry = new ZipEntry(fileOrDirectory);
                try {
                    zipOutputStream.putNextEntry(entry);
                    byte[] bytes = Files.readAllBytes(Paths.get(fileOrDirectory));
                    zipOutputStream.write(bytes, 0, bytes.length);
                } catch (IOException ex) {
                    ZIP_AS_STREAM_LOGGER.error(ex);
                }
            });

            /*ZipEntry entry = new ZipEntry("test.txt");
            zipOutputStream.putNextEntry(entry);*/
            //zipOutputStream.write(s.getBytes());
            zipOutputStream.closeEntry();
            return byteOutputStream;
            /* use more Entries to add more files
               and use closeEntry() to close each file entry */
        } catch (IOException ioe) {
            ZIP_AS_STREAM_LOGGER.error(ioe);
        }
        return null;
    }

    public static ByteArrayOutputStream zipAsStreams(List<String> directoryOrFileList) {
        
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteOutputStream)) {

            directoryOrFileList.forEach((String fileOrDirectory) -> {
                
                ZIP_AS_STREAM_LOGGER.debug("File Directory : " + fileOrDirectory);

                File file = new File(fileOrDirectory);
                if (file.isDirectory()) {
                    createAZipFile(fileOrDirectory, zipOutputStream);
                } else {
                    try {
                        createAZipFileWithFiles(file, zipOutputStream);
                    } catch (IOException ex) {
                        ZIP_AS_STREAM_LOGGER.error(ex);
                    }
                }
            });

            zipOutputStream.closeEntry();
            return byteOutputStream;
        } catch (IOException ioe) {
            ZIP_AS_STREAM_LOGGER.error(ioe);
        }
        return null;
    }

    public static void createAZipFile(String fileOrDirectory, ZipOutputStream zipOutputStream) {
        File file = new File(fileOrDirectory);

        for (final File fileEntry : Objects.requireNonNull(file.listFiles())) {

            ZIP_AS_STREAM_LOGGER.debug(fileEntry.getAbsolutePath());

            try {
                if (fileEntry.isDirectory()) {
                    createAZipFile(fileEntry.getAbsolutePath(), zipOutputStream);
                } else {
                    /*ZipEntry entry = new ZipEntry(fileEntry.getAbsolutePath());
                    zipOutputStream.putNextEntry(entry);
                    byte[] bytes = Files.readAllBytes(Paths.get(fileEntry.getAbsolutePath()));
                    zipOutputStream.write(bytes, 0, bytes.length);*/
                    createAZipFileWithFiles(fileEntry, zipOutputStream);
                }

            } catch (IOException ex) {
                ZIP_AS_STREAM_LOGGER.equals(ex);
            }
        }
    }

    public static void createAZipFileWithFiles(File file, ZipOutputStream zipOutputStream) throws IOException {
        ZIP_AS_STREAM_LOGGER.debug(file.getAbsolutePath());

        try {
            ZIP_AS_STREAM_LOGGER.debug("File is : " + file.getAbsolutePath());
            ZIP_AS_STREAM_LOGGER.debug("Zipping : " + file.getAbsolutePath());
            ZipEntry entry = new ZipEntry(file.getAbsolutePath());
            zipOutputStream.putNextEntry(entry);
            byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            zipOutputStream.write(bytes, 0, bytes.length);
            ZIP_AS_STREAM_LOGGER.debug("Zipping Completed for : " + file.getAbsolutePath());
            ZIP_AS_STREAM_LOGGER.info("Zipped : " + file.getAbsolutePath());

        } catch (IOException exp) {
            ZIP_AS_STREAM_LOGGER.error(exp);
            throw exp;
        }
    }
}
