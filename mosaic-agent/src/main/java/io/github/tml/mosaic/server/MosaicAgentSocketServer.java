package io.github.tml.mosaic.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.tml.mosaic.MosaicChunkAgent;
import io.github.tml.mosaic.util.JavaStringToFileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author welsir
 * @description :
 * @date 2025/6/7
 */
@Slf4j
public class MosaicAgentSocketServer {

    public void start(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            try (Socket socket = serverSocket.accept()) {
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                int length = dis.readInt();
                byte[] jsonBytes = new byte[length];
                dis.readFully(jsonBytes);

                String json = new String(jsonBytes, StandardCharsets.UTF_8);

                Instrumentation instrumentation = MosaicChunkAgent.getInstrumentation();

                ClassLoader targetClassLoader = null;
                Class<?> dtoClazz = null;
                for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                    if ("io.github.tml.mosaic.entity.DTO.AgentServerRequestDTO".equals(clazz.getName())) {
                        dtoClazz = clazz;
                        targetClassLoader = clazz.getClassLoader();
                        break;
                    }
                }
                if (targetClassLoader == null) {
                    throw new RuntimeException("Cannot find class loader for AgentServerRequestDTO");
                }

                Thread.currentThread().setContextClassLoader(targetClassLoader);

                ParserConfig parserConfig = new ParserConfig();
                parserConfig.setDefaultClassLoader(targetClassLoader);

                Object reqObj = JSON.parseObject(json, dtoClazz, parserConfig, Feature.SupportAutoType);
                Method getClassNameMethod = dtoClazz.getMethod("getClassName");
                String className = (String) getClassNameMethod.invoke(reqObj);

                Method getClassCodeMethod = dtoClazz.getMethod("getClassCode");
                String classCode = (String) getClassCodeMethod.invoke(reqObj);


                Class<?> targetClass = null;
                for (Class<?> allLoadedClass : instrumentation.getAllLoadedClasses()) {
                    if(allLoadedClass.getName().equals(className)){
                        targetClass = allLoadedClass;
                    }
                }

                if(targetClass == null){
                    System.out.println("shit");
                    return;
                }

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

                        String fatJarPathFromClasspath = JavaStringToFileUtil.getFatJarPathFromClasspath();

                        File fatJarFile = null;
                        if (fatJarPathFromClasspath == null) {
                            throw new RuntimeException("无法获取fat jar 路径");
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

                        realJarPaths.addAll(jarPaths);
                    } else {
                        System.out.println("Unsupported URI scheme: " + s);
                    }
                }

                String classpath = String.join(File.pathSeparator, realJarPaths);

                //内存中 编译class
                byte[] replace = JavaStringToFileUtil.compile(className,classCode,classpath);
                //热替换
                instrumentation
                        .redefineClasses(new ClassDefinition(targetClass,replace));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Class<?> findLoadedClass(String className) {
        for (Class<?> clazz : MosaicChunkAgent.getInstrumentation().getAllLoadedClasses()) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }
        return null;
    }
}