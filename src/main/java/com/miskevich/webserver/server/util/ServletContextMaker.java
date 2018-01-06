package com.miskevich.webserver.server.util;

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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ServletContextMaker implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static final int BUFFER_SIZE = 8192;
    private static final String CLASSES = "classes";
    private static final String LIB = "lib";

    private String zipFileName;
    private String applicationName;
    private File destinationDirectoryForUnzippedFile;
    private ServletContext context;

    public ServletContextMaker(String zipFileName, ServletContext context) {
        this.zipFileName = zipFileName;
        this.context = context;
    }

    public ServletContextMaker(ServletContext context) {
        this.context = context;
    }

    File generateDirectoryNames(String zipFileName) {
        String extension = zipFileName.substring(zipFileName.lastIndexOf('.'));
        destinationDirectoryForUnzippedFile = new File(zipFileName.substring(0, zipFileName.length() - extension.length()));
        String unzippedFileAbsolutePath = destinationDirectoryForUnzippedFile.getAbsolutePath();
        applicationName = unzippedFileAbsolutePath.substring(unzippedFileAbsolutePath.lastIndexOf("/"));
        return Paths.get(unzippedFileAbsolutePath, "WEB-INF").toFile();
    }

    void unzip() {
        if (destinationDirectoryForUnzippedFile.exists()) {
            LOG.info("Directory " + destinationDirectoryForUnzippedFile + " has already exists. Removing it and re-creating.");
            try {
                FileUtils.deleteDirectory(destinationDirectoryForUnzippedFile);
            } catch (IOException e) {
                LOG.error("Error: Directory " + destinationDirectoryForUnzippedFile + " wasn't removed while unzipping existing resources!", e);
                throw new RuntimeException(e);
            }
        }
        if (!destinationDirectoryForUnzippedFile.mkdir()) {
            LOG.error("Directory wasn't created: " + destinationDirectoryForUnzippedFile);
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
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFilePath)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    String findWebXml(File webXmlDirectory) throws IOException {
        FilenameFilter filter = (dir, name) -> name.equals("web.xml");
        File[] listFiles = webXmlDirectory.listFiles(filter);
        if (listFiles != null && listFiles.length == 1) {
            return listFiles[0].toString();
        } else if (listFiles == null) {
            LOG.error("web.xml doesn't exist in " + webXmlDirectory + "!!!");
            throw new FileNotFoundException("web.xml doesn't exist in " + webXmlDirectory + "!!!");
        }
        LOG.error("More than 1 web.xml was found!!!");
        throw new IOException("More than 1 web.xml was found!!!");
    }

    ServletContext initializeServlets(List<ServletDefinition> servletDefinitions, File webXmlDirectory) {
        try {
            File classesDirectory = Paths.get(webXmlDirectory.getPath(), CLASSES).toFile();
            File libDirectory = new File(Paths.get(webXmlDirectory.toString(), LIB).toString());
            File[] jarFiles = getJarsList(libDirectory);

            URL[] urls = new URL[jarFiles.length + 1];
            for (int i = 0; i < jarFiles.length; i++) {
                urls[i] = jarFiles[i].toURI().toURL();
            }
            urls[urls.length - 1] = classesDirectory.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(urls);

            for (ServletDefinition servletDefinition : servletDefinitions) {
                System.out.println("servletDefinition: " + servletDefinition);

                Class<?> clazz = classLoader.loadClass(servletDefinition.getClassName());

                HttpServlet instance = (HttpServlet) clazz.newInstance();
                for (String url : servletDefinition.getUrls()) {
                    context.addServlet(applicationName + url, instance);
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException e) {
            LOG.error("Error: ", e);
            throw new RuntimeException(e);
        }
        return context;
    }

//    void loadLibs(File webXmlDirectory) {
//        File libDirectory = new File(Paths.get(webXmlDirectory.toString(), LIB).toString());
//        File[] jars = getJarsList(libDirectory);
//
//        JarFile jarFile = new JarFile(jar);
//        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
//        URL[] urls = {new URL("jar:file:" + jar + "!/")};
//        URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls);
//
//        for (File jar : jars) {
//            try {
//
//
//                while (jarEntryEnumeration.hasMoreElements()) {
//                    JarEntry jarEntry = jarEntryEnumeration.nextElement();
//                    if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
//                        continue;
//                    }
//                    String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
//                    className = className.replace('/', '.');
//                    System.out.println("Loading className: " + className);
//                    urlClassLoader.loadClass(className);
//                }
//
//            } catch (IOException | ClassNotFoundException e) {
//                LOG.error("ERROR", e);
//                throw new RuntimeException(e);
//            }
//            LOG.info("Finished for JAR: " + jar);
//        }
//        LOG.info("Classes were loaded for libs, libDirectory: " + libDirectory);
//    }


    private File[] getJarsList(File libDirectory) {
        FilenameFilter filter = (dir, name) -> name.endsWith(".jar");
        return libDirectory.listFiles(filter);
    }

    public void initializeExistingContext() {
        //TODO: find all not war dirs, for each dir : dirs

        File webXmlDirectory = Paths.get("dir", "WEB-INF").toFile();
        initializeContext(webXmlDirectory);
    }

    private void initializeContext(File webXmlDirectory) {
        String webXml;
        try {
            webXml = findWebXml(webXmlDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        XMLServletReader xmlServletReader = new XMLServletReader();
        List<ServletDefinition> servletDefinitions = xmlServletReader.getServlets(webXml);
        //loadLibs(webXmlDirectory);
        initializeServlets(servletDefinitions, webXmlDirectory);
    }

    @Override
    public void run() {
        File webXmlDirectory = generateDirectoryNames(zipFileName);
        unzip();
        initializeContext(webXmlDirectory);
    }
}
