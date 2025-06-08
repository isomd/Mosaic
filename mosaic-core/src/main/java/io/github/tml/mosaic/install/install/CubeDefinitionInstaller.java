package io.github.tml.mosaic.install.install;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;
import io.github.tml.mosaic.install.support.InfoContext;

/**
 * 描述: CubeDefinition安装器接口
 * @author suifeng
 * 日期: 2025/6/7 
 */
public interface CubeDefinitionInstaller {

    CubeDefinitionRegistry getRegistry();
    void installCubeDefinition(InfoContext infoContext) throws CubeException;
    void setRegistry(CubeDefinitionRegistry registry);
}
