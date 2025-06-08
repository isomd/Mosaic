package io.github.tml.mosaic.install.chunk;

import io.github.tml.mosaic.util.EnvironmentPathFindUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.CodeSource;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
@Component
@Slf4j
public class ChunkLoaderInstaller implements CommandLineRunner {

    @Override
    public void run(String... args) throws ClassNotFoundException {
        String pid = getCurrentPid();

        Class<?> currentClass = Class.forName(ChunkLoaderInstaller.class.getName());
        Class<?> agentClass = Class.forName("io.github.tml.mosaic.MosaicChunkAgent");
        try {
            String currentPath = EnvironmentPathFindUtil.getJarPath(currentClass);

            String agentPath = EnvironmentPathFindUtil.getJarPath(agentClass);
            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(
                    javaBin,
                    "-cp", currentPath,
                    "io.github.tml.mosaic.install.chunk.MosaicChunkAgentInstallScript",
                    pid,
                    agentPath
            );

            log.info("检测到的agent安装器路径:{}",currentPath);
            log.info("检测到的agent脚本路径:{}",agentPath);

            pb.inheritIO();
            Process process = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }
}