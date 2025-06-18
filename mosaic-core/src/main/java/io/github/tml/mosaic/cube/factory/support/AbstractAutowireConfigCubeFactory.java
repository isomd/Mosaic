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
 * 描述: 第三步：属性填充之后的配置校验与设置操作
 * 职责：专门负责配置定义设置、配置提取、配置校验和配置注入
 * @author suifeng
 * 日期: 2025/6/6
 */
@Slf4j
public abstract class AbstractAutowireConfigCubeFactory extends AbstractAutowirePropertyCubeFactory {

    @Override
    protected Cube initializeCube(Cube cube, CubeDefinition cubeDefinition, Object[] args) throws CubeException {
        // 先进行配置处理，再交给子类进行初始化
        Cube configuredCube = processConfigurationPhase(cube, cubeDefinition, args);
        return executeInitializationPhase(configuredCube, cubeDefinition, args);
    }

    /**
     * 配置处理阶段 - 核心模板方法
     */
    protected final Cube processConfigurationPhase(Cube cube, CubeDefinition cubeDefinition, Object[] args) throws CubeException {
        try {
            log.debug("开始配置处理阶段 | CubeId: {}", cube.getCubeId());

            // 1. 设置配置定义信息
            setupConfigDefinition(cube, cubeDefinition);

            // 2. 提取配置参数
            Map<String, Object> extractedConfigs = extractConfigs(args);

            // 3. 执行配置校验与设置
            validateAndApplyConfigs(cube, extractedConfigs);

            log.info("✓ 配置处理完成 | CubeId: {}, 最终配置项: {}", cube.getCubeId(), cube.getAllConfigs().size());

            return cube;
        } catch (CubeException e) {
            log.error("✗ 配置处理失败 | CubeId: {}, 错误: {}", cube.getCubeId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorMsg = String.format("配置处理异常 | CubeId: %s", cube.getCubeId());
            log.error("✗ {}: {}", errorMsg, e.getMessage(), e);
            throw new CubeException(errorMsg + ": " + e.getMessage(), e);
        }
    }

    /**
     * 设置配置定义信息
     */
    protected void setupConfigDefinition(Cube cube, CubeDefinition cubeDefinition) throws CubeException {
        ConfigInfo configInfo = cubeDefinition.getConfigInfo();
        if (configInfo != null) {cube.setConfigInfo(configInfo);log.debug("✓ 配置定义已设置 | CubeId: {}, 配置项数量: {}", cube.getCubeId(), configInfo.getConfigItemCount());
        } else {
            log.debug("无配置定义 | CubeId: {}", cube.getCubeId());
        }
    }

    /**
     * 配置校验与应用
     * 关键改进：无论configs是否为空都必须执行，确保默认值设置和校验完整性
     */
    protected void validateAndApplyConfigs(Cube cube, Map<String, Object> configs) throws CubeException {
        try {
            // 始终调用setConfigs，让ConfigurableEntity处理完整的配置流程
            cube.setConfigs(configs);
        } catch (CubeException e) {
            // 配置校验异常，增强错误信息
            String enhancedMsg = String.format("配置校验失败 | CubeId: %s | 原因: %s", cube.getCubeId(), e.getMessage());
            throw new CubeException(enhancedMsg, e);
        }
    }

    /**
     * 从参数中提取配置
     */
    protected Map<String, Object> extractConfigs(Object[] args) {
        Map<String, Object> configs = new HashMap<>();

        if (args == null || args.length == 0) {
            log.debug("无配置参数传入");
            return configs;
        }

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            try {
                if (arg instanceof Map) {
                    extractFromMap(configs, (Map<?, ?>) arg);
                } else if (arg instanceof Properties) {
                    extractFromProperties(configs, (Properties) arg);
                } else if (arg != null) {
                    log.debug("跳过非配置参数 [索引: {}]: {}", i, arg.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.warn("提取配置失败 [索引: {}]: {}", i, e.getMessage());
            }
        }

        log.debug("配置提取完成，共提取 {} 个配置项", configs.size());
        return configs;
    }

    /**
     * 从Map中提取配置
     */
    private void extractFromMap(Map<String, Object> configs, Map<?, ?> map) {
        map.forEach((k, v) -> {
            if (k instanceof String) {
                configs.put((String) k, v);
            } else if (k != null) {
                // 尝试将非String键转换为String
                String stringKey = k.toString();
                configs.put(stringKey, v);
                log.debug("键类型转换: {} -> {}", k.getClass().getSimpleName(), stringKey);
            }
        });
    }

    /**
     * 从Properties中提取配置
     */
    private void extractFromProperties(Map<String, Object> configs, Properties props) {
        props.forEach((k, v) -> {
            if (k != null) {
                configs.put(k.toString(), v);
            }
        });
    }

    /**
     * 执行初始化阶段 - 抽象方法，由子类实现具体的初始化逻辑
     */
    protected abstract Cube executeInitializationPhase(Cube cube, CubeDefinition cubeDefinition, Object[] args) throws CubeException;
}