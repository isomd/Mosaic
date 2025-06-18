package io.github.tml.mosaic.cube.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.cube.Cube;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 描述: 第三步：属性填充之后的cube初始化操作
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowireInitCubeFactory extends AbstractAutowirePropertyCubeFactory {

    @Override
    protected Cube initializeCube(Cube cube, CubeDefinition cubeDefinition, Object[] args) {
        try {
            // 1. 设置配置定义
            ConfigInfo configInfo = cubeDefinition.getConfigInfo();
            if (configInfo != null) {
                cube.setConfigInfo(configInfo);
            }

            // 2. 注入配置参数
            Map<String, Object> configs = extractConfigs(args);
            if (!configs.isEmpty()) {
                cube.setConfigs(configs);
                log.info("✓ Config injected | CubeId: {}, configs: {}", cube.getCubeId(), configs.keySet());
            }

            // 3. 执行初始化
            if (cube.init()) {
                log.info("✓ Cube init success | CubeId: {}, CubeName: {}",
                        cube.getCubeId(), cube.getMetaData().getName());
                return cube;
            } else {
                throw new CubeException("Cube init failed, cubeId:" + cube.getCubeId());
            }
        } catch (CubeException e) {
            throw new CubeException("Config validation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CubeException("Cube init failed: " + e.getMessage(), e);
        }
    }

    /**
     * 从参数中提取配置
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractConfigs(Object[] args) {
        Map<String, Object> configs = new HashMap<>();

        if (args == null || args.length == 0) {
            return configs;
        }
        for (Object arg : args) {
            if (arg instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) arg;
                map.forEach((k, v) -> {
                    if (k instanceof String) {
                        configs.put((String) k, v);
                    }
                });
            } else if (arg instanceof Properties) {
                Properties props = (Properties) arg;
                props.forEach((k, v) -> configs.put(k.toString(), v));
            }
        }
        return configs;
    }
}
