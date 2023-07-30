package com.bervan.ieentities;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

@Slf4j
public class LoadIEAvailableEntities {

    public List<Class<?>> getSubclassesOfExcelEntity(String... basePackages) {
        if (basePackages == null || basePackages.length < 1) {
            log.error("At least one base package should be provided!");
            throw new RuntimeException("At least one base package should be provided!");
        }
        List<Class<?>> subclasses = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources("");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                getAllClassesFromPath(classLoader, file, subclasses, basePackages);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return subclasses;
    }

    private void getAllClassesFromPath(ClassLoader classLoader, File file,
                                       List<Class<?>> classes,
                                       String[] basePackages) {
        if (file.isDirectory()) {
            for (File nestedFile : file.listFiles()) {
                getAllClassesFromPath(classLoader, nestedFile, classes, basePackages);
            }
        } else if (file.getName().endsWith(".class")) {
            String classPath = file.getPath()
                    .replace(System.getProperty("file.separator"), ".")
                    .replace(".class", "")
                    .replaceFirst(".*classes\\.", "")
                    .replaceFirst(".*test-classes\\.", "")
                    .replaceFirst("\\$.*", "");
            String finalClassPath = classPath;
            Optional<String> basePackageMatch = Arrays.stream(basePackages).filter(finalClassPath::contains).findFirst();

            if (basePackageMatch.isPresent()) {
                String basePackage = basePackageMatch.get();
                int start = classPath.indexOf(basePackage);
                classPath = classPath.substring(start);
                try {
                    Class<?> clazz = classLoader.loadClass(classPath);
                    if (ExcelIEEntity.class.isAssignableFrom(clazz) && !clazz.equals(ExcelIEEntity.class)
                            && !Modifier.isAbstract(clazz.getModifiers())) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.error("Could not load class: " + classPath);
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
