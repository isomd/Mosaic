package io.github.tml.mosaic.util;

import org.codehaus.janino.SimpleCompiler;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author welsir
 * @description :
 * @date 2025/6/7
 */
public class JavaStringToFileUtil {

    public static byte[] compile(String fullClassName, String sourceCode, ClassLoader parentLoader) throws Exception {
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(parentLoader);
        compiler.cook(sourceCode);

        // 使用 SimpleCompiler 编译后的类加载器
        ClassLoader classLoader = compiler.getClassLoader();

        // 获取 class 字节码的资源路径
        String resourceName = fullClassName.replace('.', '/') + ".class";
        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IOException("无法从 SimpleCompiler 获取 class 字节码: " + resourceName);
            }
            return is.readAllBytes();
        }
    }

    public static String getFatJarPathFromClasspath() {
        String classPath = System.getProperty("java.class.path");
        if (classPath != null && classPath.endsWith(".jar")) {
            return new File(classPath).getAbsolutePath();
        }
        return null;
    }

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