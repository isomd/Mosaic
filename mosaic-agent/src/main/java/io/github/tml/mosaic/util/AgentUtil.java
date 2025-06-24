package io.github.tml.mosaic.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import javax.tools.*;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author welsir
 * @description :
 * @date 2025/6/7
 */
public class AgentUtil {

    public static Instrumentation instrumentation;

    public static Class<?> getClassByInst(String className) {
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            if(clazz.getName().equals(className)){
                return clazz;
            }
        }
        throw new RuntimeException("无法找到对应类，请确保该类被加载 : " + className);
    }


    public static String generateClassPathByEnvironment(ClassLoader targetClassLoader) {

        try {
            //解析当前classloader下所有依赖
            ScanResult scanResult = new ClassGraph()
                    .enableAllInfo()
                    .overrideClassLoaders(targetClassLoader)
                    .scan();

            List<String> realJarPaths = new ArrayList<>();

            for (URI uri : scanResult.getClasspathURIs()) {
                String s = uri.toString();

                if (s.startsWith("file:")) {
                    // 普通文件路径，直接转 File
                    realJarPaths.add(new File(uri).getAbsolutePath());
                } else if (s.startsWith("jar:file:")) {

                    List<String> jarPaths = getJarPaths();

                    realJarPaths.addAll(jarPaths);
                } else {
                    throw new RuntimeException("不支持的URI类型");
                }
            }

            return String.join(File.pathSeparator, realJarPaths);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getJarPaths() throws IOException {
        String fatJarPathFromClasspath = AgentUtil.getFatJarPathFromClasspath();

        File fatJarFile;
        if (fatJarPathFromClasspath == null) {
            throw new RuntimeException("无法获取fat jar路径");
        }
        fatJarFile = new File(fatJarPathFromClasspath);
        File parentDir = fatJarFile.getParentFile();
        File outputDir = new File(parentDir, "mosaic/lib/");

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        List<String> jarPaths = new ArrayList<>();

        try (JarFile jarFile = new JarFile(fatJarFile)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                // 只解压 BOOT-INF/lib/*.jar
                if (name.startsWith("BOOT-INF/lib/") && name.endsWith(".jar")) {
                    String jarName = name.substring(name.lastIndexOf('/') + 1);
                    File outFile = new File(outputDir, jarName);

                    // 避免重复解压
                    if (!outFile.exists()) {
                        try (InputStream is = jarFile.getInputStream(entry);
                             OutputStream os = new FileOutputStream(outFile)) {
                            is.transferTo(os);
                        }
                    }

                    jarPaths.add(outFile.getAbsolutePath());
                }
            }
        }
        return jarPaths;
    }
    /**
     *  获取fat jar 包所在目录路径
     * @return
     */
    public static String getFatJarPathFromClasspath() {
        String classPath = System.getProperty("java.class.path");
        if (classPath != null && classPath.endsWith(".jar")) {
            return new File(classPath).getAbsolutePath();
        }
        return null;
    }

    /**
     * @param fullClassName 全限定名 只接受格式如下：com.example.controller.xxx
     * @param sourceCode 源码字符串
     * @param classPath 依赖路径
     * @return class文件字节码
     */
    public static byte[] compile(String fullClassName, String sourceCode,String classPath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject javaFileObject = new JavaSourceFromString(fullClassName, sourceCode);

        Map<String, ByteArrayOutputStream> compiledClassData = new HashMap<>();

        JavaFileManager fileManager = new ForwardingJavaFileManager<>(compiler.getStandardFileManager(null, null, null)) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
                return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compiledClassData.put(className, baos);
                        return baos;
                    }
                };
            }
        };

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, Arrays.asList("-classpath",classPath), null, List.of(javaFileObject));

        if (!task.call()) {
            throw new RuntimeException("编译失败");
        }
        System.out.println("编译输出的所有 class 名字: " + compiledClassData.keySet());
        // 获取主类名对应的字节码
        ByteArrayOutputStream baos = compiledClassData.get(fullClassName);
        if (baos == null) {
            throw new RuntimeException("找不到编译输出");
        }

        return baos.toByteArray();
    }

    // 源码字符串封装类
    static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        public JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

}