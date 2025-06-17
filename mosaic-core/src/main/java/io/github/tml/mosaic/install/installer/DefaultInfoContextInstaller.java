package io.github.tml.mosaic.install.installer;
import io.github.tml.mosaic.install.reader.core.CubeInstallationItemReader;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 描述: 可配置的InfoContext安装器
 * @author suifeng
 * 日期: 2025/6/8
 */
@Slf4j
public class DefaultInfoContextInstaller extends AdapterInfoContextInstaller {

    String[] configLocations = new String[] {};

    public DefaultInfoContextInstaller(List<CubeInstallationItemReader> cubeInstallationItemReaderList, ResourceFileAdapterRegistry adapterRegistry, String[] configLocations) {
        super(cubeInstallationItemReaderList, adapterRegistry);
        this.configLocations = configLocations;
    }

    public DefaultInfoContextInstaller(List<CubeInstallationItemReader> cubeInstallationItemReaderList, ResourceFileAdapterRegistry adapterRegistry) {
        super(cubeInstallationItemReaderList, adapterRegistry);
    }

    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }
}
