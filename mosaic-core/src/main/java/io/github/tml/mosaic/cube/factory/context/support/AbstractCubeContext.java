package io.github.tml.mosaic.cube.factory.context.support;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.factory.definition.CubeRegistrationResult;
import io.github.tml.mosaic.cube.factory.support.ListableCubeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 描述: Cube上下文抽象类实现
 * @author suifeng
 * 日期: 2025/6/7
 */
@Slf4j
public abstract class AbstractCubeContext implements CubeContext {

    /**
     * 配置信息存储容器
     * Key: YAML文件中的顶级配置键
     * Value: 对应配置键下的完整配置对象
     */
    protected final Map<String, JSONObject> configurationMap = new ConcurrentHashMap<>();

    protected abstract ListableCubeFactory getBeanFactory();

    @Override
    public Cube getCube(GUID cubeId) throws CubeException {
        return getBeanFactory().getCube(cubeId);
    }

    @Override
    public Cube getCube(GUID cubeId, Object... args) throws CubeException {
        return getBeanFactory().getCube(cubeId, args);
    }

    @Override
    public List<CubeDefinition> getAllCubeDefinitions() {
        return getBeanFactory().getAllCubeDefinitions();
    }

    @Override
    public Map<GUID, CubeDefinition> getAllCubeDefinitionMap() {
        return getBeanFactory().getAllCubeDefinitionMap();
    }

    @Override
    public boolean containsCubeDefinition(String cubeId) {
        return getBeanFactory().containsCubeDefinition(cubeId);
    }

    @Override
    public CubeRegistrationResult registerCubeDefinition(GUID cubeId, CubeDefinition cubeDefinition) {
        return getBeanFactory().registerCubeDefinition(cubeId, cubeDefinition);
    }

    @Override
    public List<CubeRegistrationResult> registerAllCubeDefinition(List<CubeDefinition> cubeDefinitionList) {
        return cubeDefinitionList.stream()
                .map(def -> {
                    GUID cubeId = new GUUID(def.getId());
                    return registerCubeDefinition(cubeId, def);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取指定键的配置信息
     *
     * @param configKey 配置键
     * @return 配置对象，如果不存在则返回空的JSONObject
     */
    public JSONObject getConfiguration(String configKey) {
        if (configKey == null || configKey.trim().isEmpty()) {
            log.warn("Attempted to get configuration with null or empty key");
            return new JSONObject();
        }

        JSONObject config = configurationMap.get(configKey);
        return config != null ? config : new JSONObject();
    }

    /**
     * 获取所有配置信息的只读视图
     *
     * @return 配置信息的不可变映射
     */
    public Map<String, JSONObject> getAllConfigurations() {
        return Collections.unmodifiableMap(configurationMap);
    }

    /**
     * 检查是否存在指定的配置键
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    public boolean hasConfiguration(String configKey) {
        return configKey != null && configurationMap.containsKey(configKey);
    }

    /**
     * 获取配置数量
     *
     * @return 配置项数量
     */
    public int getConfigurationCount() {
        return configurationMap.size();
    }
}
