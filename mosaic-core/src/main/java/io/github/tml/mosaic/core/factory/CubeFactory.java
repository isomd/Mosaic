package io.github.tml.mosaic.core.factory;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;

import java.util.List;
import java.util.Map;

/**
 * 描述: Cube工厂接口
 * @author suifeng
 * 日期: 2025/6/6
 */
public interface CubeFactory {

    // 核心方法，根据cubeId获取到一个可以直接用的Cube对象
    Cube getCube(GUID cubeId) throws CubeException;

    // 需要传一些参数
    Cube getCube(GUID cubeId, Object... args) throws CubeException;

    /**
     * 获取所有已注册的CubeDefinition列表
     * @return 不可变的CubeDefinition列表，按注册顺序排列
     */
    List<CubeDefinition> getAllCubeDefinitions();

    /**
     * 获取所有已注册的CubeDefinition映射
     * @return 不可变的GUID到CubeDefinition的映射
     */
    Map<GUID, CubeDefinition> getAllCubeDefinitionMap();

    /**
     * 检查指定CubeId是否已注册
     * @param cubeId 目标CubeId
     * @return 是否已注册
     */
    boolean containsCubeDefinition(String cubeId);

    /**
     * 获取已注册CubeDefinition的总数
     * @return 注册数量
     */
    int getCubeDefinitionCount();
}
