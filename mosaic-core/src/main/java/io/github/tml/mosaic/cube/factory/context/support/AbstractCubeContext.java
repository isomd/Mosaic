package io.github.tml.mosaic.cube.factory.context.support;

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
import java.util.stream.Collectors;

/**
 * 描述: Cube上下文抽象类实现
 * @author suifeng
 * 日期: 2025/6/7
 */
@Slf4j
public abstract class AbstractCubeContext implements CubeContext {

    protected abstract ListableCubeFactory getBeanFactory();

    @Override
    public Cube getCube(GUID cubeId) throws CubeException {
        if (cubeId == null) {
            throw new CubeException("CubeId cannot be null");
        }

        // 通过cubeId获取cube的配置信息
        Map<String, Object> configMap = getCubeConfiguration(cubeId.toString());

        // 调用带参数的getCube方法
        return getCube(cubeId, configMap);
    }

    @Override
    public Cube getCube(GUID cubeId, Object... args) throws CubeException {
        if (cubeId == null) {
            throw new CubeException("CubeId cannot be null");
        }

        // 简单判断：如果第一个参数是String，就当作configId处理
        if (args != null && args.length > 0 && args[0] instanceof String) {
            String configId = (String) args[0];
            Map<String, Object> configMap = getCubeConfiguration(cubeId.toString(), configId);

            // 构建新的参数数组，将配置Map放在第一位，其他参数后移
            Object[] newArgs = new Object[args.length];
            newArgs[0] = configMap;
            if (args.length > 1) {
                System.arraycopy(args, 1, newArgs, 1, args.length - 1);
            }
            return getBeanFactory().getCube(cubeId, newArgs);
        }

        return getBeanFactory().getCube(cubeId, args);
    }


    public void removeSingletonCube(GUID cubeId) {
        getBeanFactory().removeSingleton(cubeId);
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
    public void preInstantiateSingletons() throws CubeException {
        getAllCubeDefinitionMap().keySet().forEach(this::getCube);
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
}
