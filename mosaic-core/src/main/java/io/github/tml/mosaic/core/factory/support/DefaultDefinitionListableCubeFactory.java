package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述: Cube工厂的核心实现类
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public class DefaultDefinitionListableCubeFactory extends ListableCubeFactory {

    private Map<GUID, CubeDefinition> cubeDefinitionMap = new HashMap<>();

    @Override
    public void registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition) {
        cubeDefinitionMap.put(cubeId, cubeDefinition);
    }

    @Override
    protected CubeDefinition getCubeDefinition(GUID cubeId) throws CubeException {
        CubeDefinition cubeDefinition = cubeDefinitionMap.get(cubeId);
        if(cubeDefinition == null){
            log.warn("CubeDefinition not found for cubeId: {}", cubeId);
        }
        return cubeDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws CubeException {
        cubeDefinitionMap.keySet().forEach(this::getCube);
    }
}
