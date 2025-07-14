package io.github.tml.mosaic.cube.factory.context.support;

import com.alibaba.fastjson.JSONObject;
import io.github.tml.mosaic.core.event.DefaultMosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.MosaicEventBroadcaster;
import io.github.tml.mosaic.core.event.event.CubeConfigUpdateEvent;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.core.tools.guid.GuuidAllocator;
import io.github.tml.mosaic.core.tools.param.ConfigInfo;
import io.github.tml.mosaic.core.tools.param.ConfigItem;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.config.ConfigReader;
import io.github.tml.mosaic.cube.config.YamlConfigReader;
import io.github.tml.mosaic.cube.constant.CubeModelType;
import io.github.tml.mosaic.cube.constant.CubeScopeType;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract Cube context for loading configuration from files.
 * Handles config reading, parsing, and storage with multi-configuration support.
 *
 * Author: suifeng
 * Date: 2025/6/7
 */
@Getter
@Slf4j
public abstract class AbstractConfigLoaderCubeContext extends AbstractRefreshableCubeContext {

    /** Stores multiple configuration data by cube ID, config ID */
    protected final Map<String, Map<String, JSONObject>> configurationMap = new ConcurrentHashMap<>();

    /** Default configuration identifier for each cube */
    private static final String DEFAULT_CONFIG_ID = "default";

    /** The config file reader */
    private ConfigReader configReader;

    /** GUID allocator for configuration IDs */
    private final GuuidAllocator guidAllocator = new GuuidAllocator();

    private final MosaicEventBroadcaster eventBroadcaster = DefaultMosaicEventBroadcaster.broadcaster();

    @Override
    public Map<String, Object> updateConfigurations(String cubeId, Map<String, Object> config) {
        return updateCubeConfiguration(cubeId, DEFAULT_CONFIG_ID, config);
    }

    /**
     * Update specific configuration for a cube
     */
    public Map<String, Object> updateCubeConfiguration(String cubeId, String configId, Map<String, Object> config) {
        CubeDefinition cubeDefinition = getAllCubeDefinitionMap().get(new GUUID(cubeId));
        if (cubeDefinition == null) {
            log.error("Cube definition not found for cubeId: {}", cubeId);
            throw new CubeException("Cube definition not found, cubeId: " + cubeId);
        }
        ConfigInfo configInfo = cubeDefinition.getConfigInfo();

        // Check required configs
        List<String> missing = configInfo.validateRequiredConfigs(config);
        List<String> result = new ArrayList<>(missing);

        // Validate each config item
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            ConfigItem item = configInfo.getConfigItem(name);

            if (item != null && !item.getValidationResult(value).isValid()) {
                result.add("Invalid config value: " + name + " = " + value);
            }
        }

        if (!result.isEmpty()) {
            throw new CubeException("Cube configs update failed: { " + String.join(", ", result) + " }");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(config);

        // Ensure cube configuration map exists
        configurationMap.computeIfAbsent(cubeId, k -> new ConcurrentHashMap<>());
        configurationMap.get(cubeId).put(configId, jsonObject);

        // If singleton, remove and re-initialize
        if (CubeScopeType.SINGLETON.equals(cubeDefinition.getScope())) {
            removeSingletonCube(new GUUID(cubeId));
        }

        if (CubeModelType.ANGLE_TYPE.equals(cubeDefinition.getModel())) {
            Cube cube = getCube(new GUUID(cubeId));
            CubeConfigUpdateEvent event = new CubeConfigUpdateEvent(cube.getMosaicCube(), cube.getAllConfigs(), config);
            eventBroadcaster.broadcastEvent(event);
        }

        return config;
    }

    /**
     * Add new configuration for a cube with auto-generated ID
     */
    public String addCubeConfiguration(String cubeId, Map<String, Object> config) {
        String configId = guidAllocator.nextGUID().toString();
        updateCubeConfiguration(cubeId, configId, config);
        log.info("Added new configuration for cube: {}, configId: {}", cubeId, configId);
        return configId;
    }

    /** Constructor: initialize config reader */
    public AbstractConfigLoaderCubeContext() {
        super();
        this.configReader = createDefaultConfigReader();
        log.info("[Cube][CubeContext] cubeContext load god cube configuration by configReader: {}", configReader.getClass().getSimpleName());
    }

    /** Refresh and reload all configuration resources from files */
    @Override
    protected void refreshConfigurationResources() throws CubeException {
        log.info("[Cube][CubeContext] Refreshing cube configuration resources...");

        try {
            clearConfigurations();
            String[] configLocations = getConfigLocations();

            if (configLocations == null || configLocations.length == 0) {
                log.warn("[Cube][CubeContext] No configuration locations specified, loading default configuration.");
                loadDefaultConfiguration();
                return;
            }

            log.info("Loading configuration from {} locations: {}", configLocations.length, Arrays.toString(configLocations));
            for (String configLocation : configLocations) {
                if (configLocation == null || configLocation.trim().isEmpty()) {
                    log.warn("[Cube][CubeContext] Skipping empty configuration location.");
                    continue;
                }
                loadConfigurationFromLocation(configLocation.trim());
            }

        } catch (Exception e) {
            log.error("[Cube][CubeContext] Failed to refresh cube configuration resources", e);
            throw new CubeException("Configuration refresh failed: " + e.getMessage(), e);
        }
    }

    /** Remove all stored configuration data */
    protected void clearConfigurations() {
        configurationMap.clear();
        log.info("[Cube][CubeContext] All cube configurations has cleared.");
    }

    /** Load and parse configuration from a given file location */
    private void loadConfigurationFromLocation(String configLocation) throws CubeException {
        log.debug("Loading configuration from: {}", configLocation);

        try {
            // Set config file path if using YAML reader
            if (configReader instanceof YamlConfigReader) {
                ((YamlConfigReader) configReader).setYamlFilePath(configLocation);
            }

            JSONObject configData = configReader.readConfig();
            if (configData == null || configData.isEmpty()) {
                log.warn("No configuration data found in: {}", configLocation);
                return;
            }

            parseAndStoreConfiguration(configData, configLocation);
            log.info("Loaded configuration from: {}", configLocation);

        } catch (Exception e) {
            log.error("Failed to load configuration from: {}", configLocation, e);
            throw new CubeException("Failed to load configuration from: " + configLocation, e);
        }
    }

    /** Parse configuration data and store by top-level key with default config ID */
    private void parseAndStoreConfiguration(JSONObject configData, String configLocation) {
        log.debug("Parsing configuration from: {}", configLocation);
        for (Map.Entry<String, Object> entry : configData.entrySet()) {
            String topLevelKey = entry.getKey();
            Object value = entry.getValue();

            if (topLevelKey == null || topLevelKey.trim().isEmpty()) {
                log.warn("Skipping configuration with empty key from: {}", configLocation);
                continue;
            }

            JSONObject configSection;
            if (value instanceof Map) {
                configSection = new JSONObject((Map<String, Object>) value);
            } else {
                configSection = new JSONObject();
                configSection.put("value", value);
            }
            updateConfiguration(topLevelKey, DEFAULT_CONFIG_ID, configSection);
            log.debug("Stored config section: {} from: {}", topLevelKey, configLocation);
        }
    }

    /** Load default configuration if no location is provided */
    private void loadDefaultConfiguration() {
        log.info("[Cube][CubeContext] Loading default cube configuration...");
        try {
            JSONObject defaultConfig = configReader.readConfig();
            if (defaultConfig != null && !defaultConfig.isEmpty()) {
                parseAndStoreConfiguration(defaultConfig, "default");
            } else {
                log.warn("[Cube][CubeContext] No default cube configuration found.");
            }
        } catch (Exception e) {
            log.error("Failed to load default configuration", e);
        }
    }

    /** Create the default config reader (YAML by default) */
    protected ConfigReader createDefaultConfigReader() {
        return new YamlConfigReader();
    }

    /** Set the config reader instance */
    public void setConfigReader(ConfigReader configReader) {
        this.configReader = Objects.requireNonNull(configReader, "ConfigReader cannot be null");
        log.info("ConfigReader set to: {}", configReader.getClass().getSimpleName());
    }

    /** Update or add configuration under a particular cube and config key */
    protected void updateConfiguration(String cubeId, String configId, JSONObject configValue) {
        if (cubeId == null || cubeId.trim().isEmpty()) {
            log.warn("Attempted to update configuration with empty cube ID.");
            return;
        }
        if (configId == null || configId.trim().isEmpty()) {
            log.warn("Attempted to update configuration with empty config ID for cube: {}", cubeId);
            return;
        }
        if (configValue == null) {
            log.warn("Attempted to update configuration with null value for cube: {}, configId: {}", cubeId, configId);
            configValue = new JSONObject();
        }

        configurationMap.computeIfAbsent(cubeId, k -> new ConcurrentHashMap<>());
        configurationMap.get(cubeId).put(configId, configValue);
        log.debug("Configuration updated for cube: {}, configId: {}", cubeId, configId);
    }

    /** Get default configuration by cube ID; return empty JSONObject if not found */
    @Override
    public Map<String, Object> getCubeConfiguration(String cubeId) {
        if (cubeId == null || cubeId.trim().isEmpty()) {
            log.warn("Attempted to get configuration with empty cube ID.");
            return new JSONObject();
        }

        Map<String, JSONObject> cubeConfigs = configurationMap.get(cubeId);
        if (cubeConfigs == null || cubeConfigs.isEmpty()) {
            log.debug("No configurations found for cube: {}", cubeId);
            return new JSONObject();
        }

        JSONObject defaultConfig = cubeConfigs.get(DEFAULT_CONFIG_ID);
        return defaultConfig != null ? convertJsonObjectToMap(defaultConfig) : new JSONObject();
    }

    /**
     * Get all configurations for a specific cube
     */
    public Map<String, Map<String, Object>> getAllCubeConfigurations(String cubeId) {
        if (cubeId == null || cubeId.trim().isEmpty()) {
            log.warn("Attempted to get all configurations with empty cube ID.");
            return Collections.emptyMap();
        }

        Map<String, JSONObject> cubeConfigs = configurationMap.get(cubeId);
        if (cubeConfigs == null || cubeConfigs.isEmpty()) {
            log.debug("No configurations found for cube: {}", cubeId);
            return Collections.emptyMap();
        }

        Map<String, Map<String, Object>> result = new HashMap<>();
        for (Map.Entry<String, JSONObject> entry : cubeConfigs.entrySet()) {
            result.put(entry.getKey(), convertJsonObjectToMap(entry.getValue()));
        }

        return result;
    }

    /**
     * Get specific configuration by cube ID and config ID
     */
    public Map<String, Object> getCubeConfiguration(String cubeId, String configId) {
        if (cubeId == null || cubeId.trim().isEmpty() || configId == null || configId.trim().isEmpty()) {
            log.warn("Attempted to get configuration with empty cube ID or config ID.");
            return new JSONObject();
        }

        Map<String, JSONObject> cubeConfigs = configurationMap.get(cubeId);
        if (cubeConfigs == null) {
            log.debug("No configurations found for cube: {}", cubeId);
            return new JSONObject();
        }

        JSONObject config = cubeConfigs.get(configId);
        return config != null ? convertJsonObjectToMap(config) : new JSONObject();
    }

    /**
     * Remove specific configuration
     */
    public boolean removeCubeConfiguration(String cubeId, String configId) {
        if (cubeId == null || cubeId.trim().isEmpty() || configId == null || configId.trim().isEmpty()) {
            log.warn("Attempted to remove configuration with empty cube ID or config ID.");
            return false;
        }

        // Cannot remove default configuration
        if (DEFAULT_CONFIG_ID.equals(configId)) {
            log.warn("Cannot remove default configuration for cube: {}", cubeId);
            return false;
        }

        Map<String, JSONObject> cubeConfigs = configurationMap.get(cubeId);
        if (cubeConfigs == null) {
            log.debug("No configurations found for cube: {}", cubeId);
            return false;
        }

        JSONObject removed = cubeConfigs.remove(configId);
        if (removed != null) {
            log.info("Removed configuration for cube: {}, configId: {}", cubeId, configId);
            return true;
        }

        return false;
    }

    /**
     * Get all configuration IDs for a specific cube
     */
    public Set<String> getCubeConfigurationIds(String cubeId) {
        if (cubeId == null || cubeId.trim().isEmpty()) {
            log.warn("Attempted to get configuration IDs with empty cube ID.");
            return Collections.emptySet();
        }

        Map<String, JSONObject> cubeConfigs = configurationMap.get(cubeId);
        return cubeConfigs != null ? new HashSet<>(cubeConfigs.keySet()) : Collections.emptySet();
    }

    /**
     * 克隆指定配置，如果不指定源配置ID则克隆默认配置
     */
    @Override
    public String cloneCubeConfiguration(String cubeId, String sourceConfigId) {
        if (cubeId == null || cubeId.trim().isEmpty()) {
            log.warn("Attempted to clone configuration with empty cube ID.");
            throw new CubeException("Cube ID cannot be null or empty");
        }

        // 如果没有指定源配置ID，则使用默认配置
        String actualSourceConfigId = (sourceConfigId == null || sourceConfigId.trim().isEmpty())
                ? DEFAULT_CONFIG_ID : sourceConfigId;

        Map<String, JSONObject> cubeConfigs = configurationMap.get(cubeId);
        if (cubeConfigs == null || cubeConfigs.isEmpty()) {
            log.error("No configurations found for cube: {}", cubeId);
            throw new CubeException("No configurations found for cube: " + cubeId);
        }

        JSONObject sourceConfig = cubeConfigs.get(actualSourceConfigId);
        if (sourceConfig == null) {
            log.error("Source configuration not found for cube: {}, configId: {}", cubeId, actualSourceConfigId);
            throw new CubeException("Source configuration not found for cube: " + cubeId + ", configId: " + actualSourceConfigId);
        }

        // 深度克隆配置
        JSONObject clonedConfig = deepCloneJsonObject(sourceConfig);

        // 生成新的配置ID
        String newConfigId = guidAllocator.nextGUID().toString();

        // 存储克隆的配置
        cubeConfigs.put(newConfigId, clonedConfig);

        log.info("Cloned configuration for cube: {}, source configId: {}, new configId: {}",
                cubeId, actualSourceConfigId, newConfigId);

        return newConfigId;
    }

    /**
     * 克隆默认配置
     */
    @Override
    public String cloneCubeConfiguration(String cubeId) {
        return cloneCubeConfiguration(cubeId, null);
    }

    /**
     * 深度克隆JSONObject
     */
    private JSONObject deepCloneJsonObject(JSONObject source) {
        if (source == null) {
            return new JSONObject();
        }

        try {
            // 使用JSON序列化和反序列化进行深度克隆
            String jsonString = source.toJSONString();
            return JSONObject.parseObject(jsonString);
        } catch (Exception e) {
            log.error("Failed to deep clone JSONObject: {}", source, e);
            throw new CubeException("Failed to clone configuration", e);
        }
    }

    /** Convert JSONObject to Map<String, Object>, supporting recursion */
    private Map<String, Object> convertJsonObjectToMap(JSONObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty()) {
            log.debug("Empty or null JSONObject, returning empty map.");
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
            log.error("Error converting JSONObject to Map: {}", jsonObject, e);
            throw new CubeException("Failed to convert configuration to map", e);
        }
        return resultMap;
    }

    /** Convert JSONArray to List<Object>, supporting recursion */
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

    /**
     * Abstract method for providing config file locations.
     * Subclasses must implement to specify config paths.
     */
    protected abstract String[] getConfigLocations();
}