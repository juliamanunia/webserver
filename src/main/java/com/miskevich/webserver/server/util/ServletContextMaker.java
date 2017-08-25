package com.miskevich.webserver.server.util;

import com.miskevich.webserver.model.ServletDefinition;
import com.miskevich.webserver.server.util.reader.XMLServletReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ServletContextMaker implements Runnable {

    private static final Path FILE_LOCATION = Paths.get("src", "main", "webapp");
    private final String CLASSES = "classes";
    private final String LIB = "lib";
    private static final int BUFFER_SIZE = 1024;
    private String zipFileName;
    private File destinationDirectoryForUnzippedFile;
    private static final Logger LOG = LoggerFactory.getLogger(ServletContextMaker.class);
    private ServletContext context;

    public ServletContextMaker(String zipFileName, ServletContext context) {
        this.zipFileName = zipFileName;
        this.context = context;
    }

    private File generateDirectoryNames(String zipFileName) {
        String extension = zipFileName.substring(zipFileName.lastIndexOf('.'));
        destinationDirectoryForUnzippedFile = new File(FILE_LOCATION + File.separator + zipFileName.substring(0, zipFileName.length() - extension.length()));
        return new File(destinationDirectoryForUnzippedFile + File.separator + "WEB-INF");
    }

    void unzip() {
        if (!destinationDirectoryForUnzippedFile.mkdir()) {
            LOG.error("Directory wasn't created: " + destinationDirectoryForUnzippedFile);
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
                        LOG.error("Directory wasn't created: " + directory);
                    }
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
            LOG.info("File " + zipFileName + " was unzipped");
        } catch (IOException e) {
            LOG.error(e.getMessage());
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
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    String findWebXml(File webXmlDirectory) throws NoSuchFieldException {
        FilenameFilter filter = (dir, name) -> name.equals("web.xml");
        File[] listFiles = webXmlDirectory.listFiles(filter);
        if (listFiles != null && listFiles.length == 1) {
            return listFiles[0].toString();
        }
        LOG.error("web.xml doesn't exist in " + webXmlDirectory + "!!!");
        throw new NoSuchFieldException("web.xml doesn't exist in " + webXmlDirectory + "!!!");
    }

    ServletContext initializeServlets(List<ServletDefinition> servletDefinitions, File webXmlDirectory) {
        for (ServletDefinition servletDefinition : servletDefinitions) {
            try {
                File classesDirectory = new File(Paths.get(webXmlDirectory.toString(), CLASSES).toString());
                URL[] urls = {classesDirectory.toURI().toURL()};
                URLClassLoader classLoader = new URLClassLoader(urls);
                Class<?> clazz = classLoader.loadClass(servletDefinition.getClassName());

                HttpServlet instance = (HttpServlet) clazz.newInstance();
                for (String url : servletDefinition.getUrls()) {
                    context.addServlet(url, instance);
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException e) {
                LOG.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return context;
    }

    void loadLibs(File webXmlDirectory) {
        File libDirectory = new File(Paths.get(webXmlDirectory.toString(), LIB).toString());
        try {
            URL[] urls = {libDirectory.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);
            File[] jars = getJarsList(libDirectory);
            for (File jar : jars) {
                System.out.println(jar);
                List<String> classes = getClassesList(jar);
                for (String clazz : classes) {
                    System.out.println(clazz);
                    classLoader.loadClass(clazz);
                }
            }
        } catch (MalformedURLException | ClassNotFoundException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private File[] getJarsList(File libDirectory) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".jar");
        return libDirectory.listFiles(filter);
    }

    private List<String> getClassesList(File jar) {
        List<String> classes = new ArrayList<>();
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(jar)));
            for (ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    classes.add(className.substring(0, className.length() - ".class".length()));
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return classes;
    }

    public void run() {
        File webXmlDirectory = generateDirectoryNames(zipFileName);
        unzip();

        String webXml;
        try {
            webXml = findWebXml(webXmlDirectory);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        XMLServletReader xmlServletReader = new XMLServletReader();
        List<ServletDefinition> servletDefinitions = xmlServletReader.getServlets(webXml);
        initializeServlets(servletDefinitions, webXmlDirectory);

    }
}
