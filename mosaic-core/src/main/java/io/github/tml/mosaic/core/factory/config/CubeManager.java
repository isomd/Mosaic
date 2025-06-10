package io.github.tml.mosaic.core.factory.config;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 描述: Cube管理器接口
 * 管理所有Cube实例，支持同一CubeId的多个实例
 * @author suifeng
 * 日期: 2025/6/11
 */
public interface CubeManager {

    /**
     * 注册Cube实例到管理器，自动分配实例ID
     * @param cubeId Cube类型ID
     * @param cube Cube实例
     * @return 自动分配的实例ID
     */
    GUID registerCube(GUID cubeId, Cube cube);

    /**
     * 通过实例ID移除Cube
     * @param instanceId 实例ID
     * @return 是否移除成功
     */
    boolean removeCubeByInstanceId(GUID instanceId);

    /**
     * 移除指定CubeId的所有实例
     * @param cubeId Cube类型ID
     * @return 移除的实例数量
     */
    int removeCubesByCubeId(GUID cubeId);

    /**
     * 通过实例ID获取Cube
     * @param instanceId 实例ID
     */
    Cube getCubeByInstanceId(GUID instanceId);

    /**
     * 获取指定CubeId的所有实例
     * @param cubeId Cube类型ID
     */
    List<Cube> getCubesByCubeId(GUID cubeId);

    /**
     * 获取指定CubeId的第一个实例（常用于获取默认实例）
     * @param cubeId Cube类型ID
     */
    Cube getFirstCubeByCubeId(GUID cubeId);

    /**
     * 检查是否存在指定CubeId的实例
     * @param cubeId Cube类型ID
     */
    boolean containsCubeId(GUID cubeId);

    /**
     * 检查指定实例ID是否存在
     * @param instanceId 实例ID
     */
    boolean containsInstanceId(GUID instanceId);

    /**
     * 获取所有已注册的CubeId
     */
    Set<GUID> getAllCubeIds();

    /**
     * 获取所有实例ID
     */
    Set<GUID> getAllInstanceIds();

    /**
     * 获取指定CubeId的实例数量
     * @param cubeId Cube类型ID
     */
    int getCubeInstanceCount(GUID cubeId);

    /**
     * 获取管理器中的总实例数量
     */
    int getTotalInstanceCount();

    /**
     * 获取所有Cube实例的映射关系 (实例ID -> Cube)
     */
    Map<GUID, Cube> getAllCubeInstances();

    /**
     * 清空管理器中的所有Cube
     */
    void clear();
}