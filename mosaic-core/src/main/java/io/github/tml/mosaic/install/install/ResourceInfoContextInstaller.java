package io.github.tml.mosaic.install.install;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.install.InstallationConfig;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.install.reader.CubeInstallationItemReader;

import io.github.tml.mosaic.install.enhance.InstallationConfigEnhancer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: CubeDefinition安装抽象类
 * @author suifeng
 * 日期: 2025/6/8
 */
@Data
public abstract class ResourceInfoContextInstaller extends AbstractInfoContextInstaller {

    protected List<CubeInstallationItemReader> cubeInstallationItemReaderList;
    protected List<InstallationConfigEnhancer> installationConfigEnhancers;

    public ResourceInfoContextInstaller(List<CubeInstallationItemReader> cubeInstallationItemReaderList) {
        this.cubeInstallationItemReaderList = cubeInstallationItemReaderList;
    }

    @Override
    protected InstallationConfig readInstallationConfigForResource(String[] configLocations) throws CubeException {

        List<InstallationItem> itemList = new ArrayList<>();
        for (CubeInstallationItemReader reader : cubeInstallationItemReaderList) {
            InstallationConfig installationConfig = reader.loadCubeInstallationItem(configLocations);
            if (installationConfig != null) {
                itemList.addAll(installationConfig.getInstallations());
            }
        }

        InstallationConfig installationConfig = new InstallationConfig();
        installationConfig.setInstallations(itemList);

        // TODO InstallationConfig 增强处理
        if (null != installationConfigEnhancers  && !installationConfigEnhancers.isEmpty()) {
            for (InstallationConfigEnhancer installationConfigEnhancer : installationConfigEnhancers) {
                installationConfigEnhancer.enhance(installationConfig);
            }
        }

        return installationConfig;
    }
}
