package io.github.tml.mosaic.cube.factory.context;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.CubeFactory;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    Map<String, Map<String, Object>> getAllCubeConfigurations(String cubeId);

    Map<String, Object> getCubeConfiguration(String cubeId, String configId);

    Map<String, Object> updateCubeConfiguration(String cubeId, String configId, Map<String, Object> config);

    boolean removeCubeConfiguration(String cubeId, String configId);

    /**
     * 克隆指定配置，如果不指定源配置ID则克隆默认配置
     * @param cubeId 目标cube ID
     * @param sourceConfigId 源配置ID，如果为null或空则克隆默认配置
     * @return 新生成的配置ID
     */
    String cloneCubeConfiguration(String cubeId, String sourceConfigId);

    /**
     * 克隆默认配置
     * @param cubeId 目标cube ID
     * @return 新生成的配置ID
     */
    String cloneCubeConfiguration(String cubeId);
}
