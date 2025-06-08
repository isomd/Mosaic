package io.github.tml.mosaic.install.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
@Component
@Slf4j
public class ChunkLoaderInstaller implements CommandLineRunner {

    @Override
    public void run(String... args) {
        String pid = getCurrentPid();
        String jarPath = getJarPath(io.github.tml.mosaic.MosaicChunkAgent.class);
        String currentJar = getCurrentJarPath();
        try {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(
                    javaBin,
                    "-cp", currentJar,
                    "io.github.tml.mosaic.install.chunk.MosaicChunkAgentInstallScript",
                    pid,
                    jarPath
            );
            log.info("检测到的agent安装器路径:{}",currentJar);
            log.info("检测到的agent脚本路径:{}",jarPath);
            pb.inheritIO();
            Process process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJarPath(Class<?> clazz) {
        try {
            return Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (Exception e) {
            throw new RuntimeException("无法获取jar路径", e);
        }
    }

    private String getCurrentJarPath() {
        try {
            return new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (Exception e) {
            throw new RuntimeException("无法获取当前 jar 路径", e);
        }
    }

    private String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }
}