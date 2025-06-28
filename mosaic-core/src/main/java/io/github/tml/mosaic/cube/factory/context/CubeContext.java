package io.github.tml.mosaic.cube.factory.context;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.CubeFactory;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;

import java.util.List;
import java.util.Map;

/**
 * 描述: Cube上下文接口
 * @author suifeng
 * 日期: 2025/6/7
 */
public interface CubeContext extends CubeFactory {

    CubeRegistrationResult registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition);

    List<CubeRegistrationResult> registerAllCubeDefinition(List<CubeDefinition> cubeDefinitionList);

    void preInstantiateSingletons() throws CubeException;

    Map<String, Object> getCubeConfiguration(String cubeId);

    Map<String, Object> updateConfigurations(String cubeId, Map<String, Object> config);
}
