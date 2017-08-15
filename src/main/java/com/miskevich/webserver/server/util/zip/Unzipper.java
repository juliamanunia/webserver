package com.miskevich.webserver.server.util.zip;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {

    private static final Path FILE_LOCATION = Paths.get("src", "main", "webapp");
    private static final int BUFFER_SIZE = 1024;
    private String zipFileName;
    private File webXmlDirectory;
    private File destinationDirectoryForUnzippedFile;

    public Unzipper(String zipFileName) {
        this.zipFileName = zipFileName;

        String extension = zipFileName.substring(zipFileName.lastIndexOf('.'));
        destinationDirectoryForUnzippedFile = new File(FILE_LOCATION + File.separator + zipFileName.substring(0, zipFileName.length() - extension.length()));
        webXmlDirectory = new File(destinationDirectoryForUnzippedFile + File.separator + "WEB-INF");
    }

    public void unzip() {
        if (!destinationDirectoryForUnzippedFile.mkdir()) {
            System.out.println("Dir wasn't created: " + destinationDirectoryForUnzippedFile);
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(FILE_LOCATION + File.separator + zipFileName)))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                String destinationFilePath = destinationDirectoryForUnzippedFile + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipInputStream, destinationFilePath);
                } else {
                    File directory = new File(destinationFilePath);
                    if (!directory.mkdir()) {
                        System.out.println("Dir wasn't created: " + directory);
                    }
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
            System.out.println("File " + zipFileName + " was unzipped");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void extractFile(ZipInputStream zipInputStream, String destinationFilePath) {
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destinationFilePath))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String findWebXml() {
        FilenameFilter filter = (dir, name) -> name.equals("web.xml");
        File[] listFiles = webXmlDirectory.listFiles(filter);
        return listFiles != null && listFiles.length == 1 ? listFiles[0].toString() : "web.xml doesn't exist in " + webXmlDirectory + "!!!";
    }
}
