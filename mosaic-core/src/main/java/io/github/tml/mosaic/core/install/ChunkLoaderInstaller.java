package io.github.tml.mosaic.core.install;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
@Component
public class ChunkLoaderInstaller implements CommandLineRunner {

    @Override
    public void run(String... args) {
        String pid = getCurrentPid();
        String jarPath = getJarPath(org.springframework.boot.SpringApplication.class);
        String currentJar = getCurrentJarPath();
        try {
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(
                    javaBin,
                    "-cp", currentJar,
                    "com.agentscript.core.MosaicChunkAgentInstall",
                    pid,
                    jarPath
            );

            pb.inheritIO();
            Process process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJarPath(Class<?> clazz) {
        try {
            return clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
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