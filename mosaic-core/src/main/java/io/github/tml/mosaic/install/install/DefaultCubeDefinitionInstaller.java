package io.github.tml.mosaic.install.install;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;
import io.github.tml.mosaic.core.factory.context.json.CubeDefinitionConverter;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.install.support.InfoContext;

import java.util.List;

/**
 * 描述: 默认的CubeDefinition安装器
 * @author suifeng
 * 日期: 2025/6/8
 */
public class DefaultCubeDefinitionInstaller extends AbstractCubeDefinitionInstaller {

    public DefaultCubeDefinitionInstaller() {
        super();
    }

    @Override
    public void installCubeDefinition(InfoContext infoContext) throws CubeException {
        // 关键转换步骤
        List<CubeDefinition> cubeDefs = CubeDefinitionConverter.convertToCubeDefinitions(infoContext);
        CubeDefinitionRegistry registry = getRegistry();
        for (CubeDefinition cubeDef : cubeDefs) {
            registry.registerCubeDefinition(cubeDef.getId(), cubeDef);
        }
    }
}
