package io.github.tml.mosaic.install.installer.core;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.install.domian.install.InstallationConfig;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.Data;
import java.util.List;

/**
 * 描述: CubeDefinition安装抽象类
 * @author suifeng
 * 日期: 2025/6/8
 */
@Data
public abstract class AbstractInfoContextInstaller implements InfoContextInstaller {

    @Override
    public List<InfoContext> installCubeInfoContext() throws CubeException {
        return installCubeInfoContext(getConfigLocations());
    }

    @Override
    public List<InfoContext> installCubeInfoContext(String[] configLocations) throws CubeException {
        InstallationConfig installationConfig = readInstallationConfigForResource(configLocations);
        return convertInstallationConfigToInfoContexts(installationConfig);
    }

    protected abstract InstallationConfig readInstallationConfigForResource(String[] configLocations) throws CubeException;

    protected abstract List<InfoContext> convertInstallationConfigToInfoContexts(InstallationConfig installationConfig);

    protected abstract String[] getConfigLocations();
}
