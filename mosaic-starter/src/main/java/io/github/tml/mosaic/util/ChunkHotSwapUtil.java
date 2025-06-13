package io.github.tml.mosaic.util;

import org.benf.cfr.reader.api.CfrDriver;
import org.benf.cfr.reader.api.OutputSinkFactory;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * @author welsir
 * @description :
 * @date 2025/6/13
 */
public class ChunkHotSwapUtil {

    private static String decompileClassToString(String classFilePath) {
        StringBuilder result = new StringBuilder();

        OutputSinkFactory mySink = new OutputSinkFactory() {
            @Override
            public List<SinkClass> getSupportedSinks(SinkType sinkType, Collection<SinkClass> collection) {
                return Collections.singletonList(SinkClass.STRING);
            }

            @Override
            public <T> Sink<T> getSink(SinkType sinkType, SinkClass sinkClass) {
                return (T content) -> {
                    if (sinkType == SinkType.JAVA && sinkClass == SinkClass.STRING) {
                        result.append(content).append(System.lineSeparator());
                    }
                };
            }

        };

        CfrDriver driver = new CfrDriver.Builder()
                .withOutputSink(mySink)
                .build();

        driver.analyse(Collections.singletonList(classFilePath));
        return result.toString();
    }

    public static String decompileClassFromClassName(String className) {
        try {
            // 将类名转换为路径
            String classResourcePath = className.replace('.', '/') + ".class";
            byte[] classBytes = getBytes(className, classResourcePath);

            // 写入临时 class 文件
            File tempFile = File.createTempFile("tmp-", ".class");
            tempFile.deleteOnExit();
            Files.write(tempFile.toPath(), classBytes);

            // 调用已有的反编译工具
            return decompileClassToString(tempFile.getAbsolutePath()).replaceFirst("(?s)^/\\*.*?\\*/\\s*", "");

        } catch (Exception e) {
            throw new RuntimeException("Decompile failed: " + className, e);
        }
    }

    private static byte[] getBytes(String className, String classResourcePath) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream(classResourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot find class file for: " + className);
        }

        // 读取字节流
        byte[] classBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            classBytes = baos.toByteArray();
        }
        return classBytes;
    }
}