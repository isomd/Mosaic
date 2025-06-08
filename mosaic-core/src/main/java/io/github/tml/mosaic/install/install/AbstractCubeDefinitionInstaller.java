package io.github.tml.mosaic.install.install;

import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;
import lombok.Data;
import lombok.Setter;

/**
 * 描述: CubeDefinition安装抽象类
 * @author suifeng
 * 日期: 2025/6/8
 */
@Setter
@Data
public abstract class AbstractCubeDefinitionInstaller implements CubeDefinitionInstaller {

    private CubeDefinitionRegistry registry;

    protected AbstractCubeDefinitionInstaller() {
        super();
    }

    @Override
    public CubeDefinitionRegistry getRegistry() {
        return registry;
    }
}
