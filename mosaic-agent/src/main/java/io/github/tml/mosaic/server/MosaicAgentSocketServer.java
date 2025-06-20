package io.github.tml.mosaic.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.tml.mosaic.MosaicChunkAgent;
import io.github.tml.mosaic.entity.DTO.AgentServerRequestDTO;
import io.github.tml.mosaic.util.AgentUtil;
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

                Class<?> dtoClazz = AgentServerRequestDTO.class;
                ClassLoader targetClassLoader = dtoClazz.getClassLoader();

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
                        break;
                    }
                }

                if(targetClass == null){
                    throw new RuntimeException("无法从对应类加载器加载class: "+targetClassLoader+" : "+className);
                }

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

                String classpath = String.join(File.pathSeparator, realJarPaths);

                //内存中 编译class
                byte[] replace = AgentUtil.compile(className,classCode,classpath);
                //热替换
                instrumentation
                        .redefineClasses(new ClassDefinition(targetClass,replace));
                System.out.println("Mosaic agent proxy finished...");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}