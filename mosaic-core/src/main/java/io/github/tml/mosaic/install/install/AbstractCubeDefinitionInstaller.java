package io.github.tml.mosaic.install.install;

import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;

/**
 * 描述: CubeDefinition安装抽象类
 * @author suifeng
 * 日期: 2025/6/8
 */
public abstract class AbstractCubeDefinitionInstaller implements CubeDefinitionInstaller {

    private final CubeDefinitionRegistry registry;

    protected AbstractCubeDefinitionInstaller(CubeDefinitionRegistry registry) {
        super();
        this.registry = registry;
    }

    @Override
    public CubeDefinitionRegistry getRegistry() {
        return registry;
    }
}
