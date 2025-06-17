package io.github.tml.mosaic.install.collector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tml.mosaic.install.collector.core.CommonInfoCollector;
import io.github.tml.mosaic.core.tools.config.ConfigInfo;
import io.github.tml.mosaic.core.tools.config.ConfigItem;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cube配置信息收集器
 * 负责从cube-config.json加载配置并分配给对应的Cube
 */
@Slf4j
public class CubeConfigInfoCollector implements CommonInfoCollector {

    private static final String CONFIG_FILE_NAME = "cube-config.json";
    private final ObjectMapper objectMapper;

    public CubeConfigInfoCollector() {
        this.objectMapper = new ObjectMapper();
        // 配置ObjectMapper以提高容错性
        this.objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS, true);
        this.objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void collect(InfoContext infoContext) {
        List<CubeInfo> cubeInfoList = infoContext.getCubeInfoList();
        if (CollectionUtils.isEmpty(cubeInfoList)) {
            log.debug("CubeConfigInfoCollector: cubeInfoList is empty, skip config collection");
            return;
        }

        long startTime = System.currentTimeMillis();

        // 加载配置文件
        List<ConfigInfo> configInfoList = loadConfigFile(infoContext);
        if (CollectionUtils.isEmpty(configInfoList)) {
            log.info("No cube config file found or config file is empty");
            return;
        }

        // 构建cubeId到配置的映射
        Map<String, ConfigInfo> configMap = buildConfigMap(configInfoList);

        // 为每个Cube分配对应的配置
        int assignedCount = assignConfigToCubes(cubeInfoList, configMap);

        log.info("CubeConfigInfoCollector completed: loaded {} configs, assigned {} cube configs in {}ms",
                configInfoList.size(), assignedCount, System.currentTimeMillis() - startTime);
    }

    /**
     * 加载配置文件
     */
    private List<ConfigInfo> loadConfigFile(InfoContext infoContext) {
        // 策略1: 从classpath根目录加载
        List<ConfigInfo> configs = loadConfigFromClasspathRoot(infoContext);
        if (!CollectionUtils.isEmpty(configs)) {
            log.debug("Successfully loaded config from classpath root");
            return configs;
        }

        // 策略2: 从resources目录加载
        configs = loadConfigFromResources(infoContext);
        if (!CollectionUtils.isEmpty(configs)) {
            log.debug("Successfully loaded config from resources directory");
            return configs;
        }

        log.debug("Config file not found in any location: {}", CONFIG_FILE_NAME);
        return Collections.emptyList();
    }

    /**
     * 从classpath根目录加载配置
     */
    private List<ConfigInfo> loadConfigFromClasspathRoot(InfoContext infoContext) {
        ClassLoader classLoader = determineClassLoader(infoContext);

        try (InputStream inputStream = classLoader.getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream == null) {
                log.debug("Config file not found in classpath root: {}", CONFIG_FILE_NAME);
                return Collections.emptyList();
            }

            List<ConfigInfo> configs = objectMapper.readValue(
                    inputStream, new TypeReference<List<ConfigInfo>>() {});

            log.debug("Successfully loaded {} cube configs from classpath root", configs.size());
            return configs;

        } catch (Exception e) {
            log.warn("Failed to load config from classpath root: {}, error: {}",
                    CONFIG_FILE_NAME, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 从resources目录加载配置
     */
    private List<ConfigInfo> loadConfigFromResources(InfoContext infoContext) {
        ClassLoader classLoader = determineClassLoader(infoContext);
        String resourcePath = "META-INF/" + CONFIG_FILE_NAME;

        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                log.debug("Config file not found in resources: {}", resourcePath);
                return Collections.emptyList();
            }

            List<ConfigInfo> configs = objectMapper.readValue(
                    inputStream, new TypeReference<List<ConfigInfo>>() {});

            log.debug("Successfully loaded {} cube configs from resources", configs.size());
            return configs;

        } catch (Exception e) {
            log.warn("Failed to load config from resources: {}, error: {}",
                    resourcePath, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 构建cubeId到配置的映射
     */
    private Map<String, ConfigInfo> buildConfigMap(List<ConfigInfo> configInfoList) {
        Map<String, ConfigInfo> configMap = new HashMap<>();

        for (ConfigInfo configInfo : configInfoList) {
            if (configInfo.getId() != null && !configInfo.getId().trim().isEmpty()) {
                String cubeId = configInfo.getId().trim();

                if (configMap.containsKey(cubeId)) {
                    log.warn("Duplicate cube config found for cubeId: {}, using the first one", cubeId);
                } else {
                    configMap.put(cubeId, configInfo);
                    log.debug("Mapped config for cubeId: {} with {} config items",
                            cubeId, configInfo.getConfigItemCount());
                }
            } else {
                log.warn("Found cube config with null or empty cubeId, skipping");
            }
        }

        return configMap;
    }

    /**
     * 为Cube分配对应的配置
     */
    private int assignConfigToCubes(List<CubeInfo> cubeInfoList,
                                    Map<String, ConfigInfo> configMap) {
        int assignedCount = 0;

        for (CubeInfo cubeInfo : cubeInfoList) {
            String cubeId = cubeInfo.getId();
            if (cubeId != null && configMap.containsKey(cubeId)) {
                ConfigInfo configInfo = configMap.get(cubeId);
                cubeInfo.setConfigInfo(configInfo);
                assignedCount++;

                log.debug("Assigned config to cube: {} with {} config items",
                        cubeId, configInfo.getConfigItemCount());

                // 验证必填配置项（可选的警告性检查）
                List<String> requiredItems = configInfo.getRequiredConfigItems()
                        .stream()
                        .map(ConfigItem::getName).collect(Collectors.toList());

                if (!requiredItems.isEmpty()) {
                    log.debug("Cube {} has {} required config items: {}",
                            cubeId, requiredItems.size(), requiredItems);
                }
            } else {
                log.debug("No config found for cube: {}", cubeId);
            }
        }

        return assignedCount;
    }

    /**
     * 确定使用的ClassLoader
     */
    private ClassLoader determineClassLoader(InfoContext infoContext) {
        ClassLoader classLoader = infoContext.getClassLoader();
        if (classLoader != null) {
            return classLoader;
        }

        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            return classLoader;
        }

        return CubeConfigInfoCollector.class.getClassLoader();
    }
}