package io.github.tml.mosaic.install.chunk;

import io.github.tml.mosaic.config.MosaicChunkConfig;
import io.github.tml.mosaic.util.EnvironmentPathFindUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
@Component
@Slf4j
public class ChunkLoaderInstaller implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    MosaicChunkConfig mosaicChunkConfig;

    private String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String pid = getCurrentPid();

        try {
            Class<?> currentClass = Class.forName(ChunkLoaderInstaller.class.getName());
            Class<?> agentClass = Class.forName("io.github.tml.mosaic.MosaicChunkAgent");
            String currentPath = EnvironmentPathFindUtil.getJarPath(currentClass);

            String agentPath = EnvironmentPathFindUtil.getJarPath(agentClass);

            log.info("install path: {}", currentPath);
            log.info("agent path: {}", agentPath);

            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ProcessBuilder pb = new ProcessBuilder(
                    javaBin,
                    "-cp", currentPath,
                    "io.github.tml.mosaic.install.chunk.MosaicChunkAgentInstallScript",
                    pid,
                    agentPath,
                    String.valueOf(mosaicChunkConfig.getPort())
            );
            log.info("attach agent to target project, listen port is : {}", mosaicChunkConfig.getPort());
            pb.inheritIO();
            Process process = pb.start();
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}