package com.fire.util;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 类路径扫描工具
 */
public class ClasspathScanner {
    
    public static Set<Class<?>> scan(String packageName) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageUrl = classLoader.getResource(packagePath);
        
        if (packageUrl != null) {
            File packageDir = new File(packageUrl.getFile());
            scanDirectory(packageDir, packageName, classes);
        }
        
        return classes;
    }
    
    private static void scanDirectory(File dir, String packageName, Set<Class<?>> classes) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    // 忽略无法加载的类
                }
            }
        }
    }
}
