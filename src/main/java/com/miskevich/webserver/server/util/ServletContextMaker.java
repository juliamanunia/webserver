package com.miskevich.webserver.server.util;

import com.miskevich.webserver.exception.WebXmlFileException;
import com.miskevich.webserver.model.ServletDefinition;
import com.miskevich.webserver.server.util.reader.XMLServletReader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ServletContextMaker implements Runnable {

    private static final int BUFFER_SIZE = 8192;
    private static final String CLASSES = "classes";
    private static final String LIB = "lib";
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private String zipFileName;
    private String applicationName;
    private File webXmlPath;
    private ServletContext context;

    public ServletContextMaker(String zipFileName, ServletContext context) {
        this.zipFileName = zipFileName;
        this.context = context;
    }

    public ServletContextMaker(ServletContext context) {
        this.context = context;
    }

    File generateDirectoryNameForUnzippedFile(String zipFileName) {
        String extension = zipFileName.substring(zipFileName.lastIndexOf('.'));
        return new File(zipFileName.substring(0, zipFileName.length() - extension.length()));
    }

    File generateDirectoryNameForWebXml(File destinationDirectoryForUnzippedFile) {
        String unzippedFileAbsolutePath = destinationDirectoryForUnzippedFile.getAbsolutePath();
        applicationName = unzippedFileAbsolutePath.substring(unzippedFileAbsolutePath.lastIndexOf("/"));
        return Paths.get(unzippedFileAbsolutePath, "WEB-INF").toFile();
    }

    void unzip(File destinationDirectoryForUnzippedFile) {
        if (destinationDirectoryForUnzippedFile.exists()) {
            LOG.info("Directory " + destinationDirectoryForUnzippedFile + " has already exists. Removing it and re-creating.");
            try {
                FileUtils.deleteDirectory(destinationDirectoryForUnzippedFile);
            } catch (IOException e) {
                LOG.warn("Directory {} wasn't removed while unzipping existing resources!", destinationDirectoryForUnzippedFile);
                throw new RuntimeException(e);
            }
        }
        if (!destinationDirectoryForUnzippedFile.mkdir()) {
            LOG.warn("Directory wasn't created: {}", destinationDirectoryForUnzippedFile);
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFileName)))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                String destinationFilePath = destinationDirectoryForUnzippedFile + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipInputStream, destinationFilePath);
                } else {
                    File directory = new File(destinationFilePath);
                    if (!directory.mkdir()) {
                        LOG.warn("Directory wasn't created: {}", directory);
                    }
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
            LOG.info("File {} was unzipped", zipFileName);
        } catch (IOException e) {
            LOG.warn("File {} wasn't unzipped", zipFileName);
            throw new RuntimeException(e);
        }
    }

    private void extractFile(ZipInputStream zipInputStream, String destinationFilePath) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFilePath)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            LOG.warn("Exception while file {} extraction", destinationFilePath);
            throw new RuntimeException(e);
        }

    }

    ServletContext initializeServlets(List<ServletDefinition> servletDefinitions, File webXmlDirectory) {
        try {
            File classesDirectory = Paths.get(webXmlDirectory.getPath(), CLASSES).toFile();
            File libDirectory = Paths.get(webXmlDirectory.getPath(), LIB).toFile();
            File[] jarFiles = getJarsList(libDirectory);

            URL[] urls = new URL[jarFiles.length + 1];
            for (int i = 0; i < jarFiles.length; i++) {
                urls[i] = jarFiles[i].toURI().toURL();
            }
            urls[urls.length - 1] = classesDirectory.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(urls);

            for (ServletDefinition servletDefinition : servletDefinitions) {
                LOG.debug("Start initialize servlet for ServletDefinition {}", servletDefinition);
                Class<?> clazz = classLoader.loadClass(servletDefinition.getClassName());

                HttpServlet instance = (HttpServlet) clazz.newInstance();
                for (String url : servletDefinition.getUrls()) {
                    context.addServlet(applicationName + url, instance);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException e) {
            LOG.warn("Servlet initialization failed");
            throw new RuntimeException(e);
        }
        return context;
    }

    private File[] getJarsList(File libDirectory) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".jar");
        return libDirectory.listFiles(filter);
    }

    boolean findWebXml(File webXmlDirectory) {
        FilenameFilter filter = (dir, name) -> name.equals("web.xml");
        File[] webXmlList = webXmlDirectory.listFiles(filter);

        if (webXmlList != null) {
            if (webXmlList.length == 0) {
                LOG.warn("web.xml doesn't exist in {} !", webXmlDirectory);
                throw new WebXmlFileException("web.xml doesn't exist in " + webXmlDirectory);
            }

            webXmlPath = webXmlList[0];
        }
        return true;
    }

    private void initializeContext(File webXmlDirectory) {
        if (findWebXml(webXmlDirectory)) {
            XMLServletReader xmlServletReader = new XMLServletReader();
            List<ServletDefinition> servletDefinitions = xmlServletReader.getServlets(webXmlPath);
            initializeServlets(servletDefinitions, webXmlDirectory);
        }
    }

    @Override
    public void run() {
        File directoryNameForUnzippedFile = generateDirectoryNameForUnzippedFile(zipFileName);
        File webXmlDirectory = generateDirectoryNameForWebXml(directoryNameForUnzippedFile);

        unzip(directoryNameForUnzippedFile);
        initializeContext(webXmlDirectory);
    }
}
