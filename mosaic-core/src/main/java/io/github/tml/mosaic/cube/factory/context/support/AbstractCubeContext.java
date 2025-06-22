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

import java.util.*;
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
        if (cubeId == null) {
            throw new CubeException("CubeId cannot be null");
        }

        // 将JSONObject转换为Map<String, Object>作为参数
        Map<String, Object> configMap = convertJsonObjectToMap(getConfiguration(cubeId.toString()));

        // 调用带参数的getCube方法
        return getCube(cubeId, configMap);
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
     * 将JSONObject转换为Map<String, Object>
     * 支持嵌套对象的递归转换，确保类型安全和性能优化
     */
    private Map<String, Object> convertJsonObjectToMap(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            log.debug("Empty or null JSONObject provided, returning empty map");
            return Collections.emptyMap();
        }

        Map<String, Object> resultMap = new HashMap<>(jsonObject.size());

        try {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof JSONObject) {
                    resultMap.put(key, convertJsonObjectToMap((JSONObject) value));
                } else if (value instanceof com.alibaba.fastjson.JSONArray) {
                    resultMap.put(key, convertJsonArrayToList((com.alibaba.fastjson.JSONArray) value));
                } else {
                    resultMap.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("Error converting JSONObject to Map for configuration: {}", jsonObject, e);
            throw new CubeException("Failed to convert configuration to map", e);
        }

        return resultMap;
    }

    /**
     * 处理JSONArray到List的转换
     */
    private List<Object> convertJsonArrayToList(com.alibaba.fastjson.JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> resultList = new ArrayList<>(jsonArray.size());

        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                resultList.add(convertJsonObjectToMap((JSONObject) item));
            } else if (item instanceof com.alibaba.fastjson.JSONArray) {
                resultList.add(convertJsonArrayToList((com.alibaba.fastjson.JSONArray) item));
            } else {
                resultList.add(item);
            }
        }

        return resultList;
    }
}
