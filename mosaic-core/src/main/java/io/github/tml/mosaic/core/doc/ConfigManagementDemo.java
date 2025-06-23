package io.github.tml.mosaic.core.doc;

import io.github.tml.mosaic.core.doc.part.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理系统演示 - 展示TML文档框架的实际应用
 * 模拟企业级配置管理场景，包含多层嵌套、动态属性和复杂验证规则
 */
public class ConfigManagementDemo {

    public static void main(String[] args) {
        System.out.println("=== TML文档框架 - 企业配置管理系统演示 ===\n");

        // 1. 构建复杂的配置数据结构
        System.out.println("--- 构建多层次配置数据 ---");
        
        // 数据库连接池配置的验证规则
        Map<String, Object> dbValidationMap = new HashMap<>();
        dbValidationMap.put("minValue", 1);
        dbValidationMap.put("maxValue", 100);
        dbValidationMap.put("pattern", "^[1-9]\\d*$");
        dbValidationMap.put("errorMessage", "连接池大小必须在1-100之间");

        // 数据库连接池配置的值定义
        Map<String, Object> dbValueMap = new HashMap<>();
        dbValueMap.put("defaultValue", 10);
        dbValueMap.put("required", true);
        dbValueMap.put("validation", dbValidationMap);

        // 数据库配置的元数据
        Map<String, Object> dbMetaDataMap = new HashMap<>();
        dbMetaDataMap.put("name", "数据库连接池配置");
        dbMetaDataMap.put("type", "INTEGER");
        dbMetaDataMap.put("desc", "控制应用程序数据库连接池的最大连接数");

        // 完整的数据库配置
        Map<String, Object> dbConfigMap = new HashMap<>();
        dbConfigMap.put("configId", "DB_POOL_SIZE");
        dbConfigMap.put("environment", "PRODUCTION");
        dbConfigMap.put("category", "DATABASE");
        dbConfigMap.put("metadata", dbMetaDataMap);
        dbConfigMap.put("value", dbValueMap);

        // 缓存配置的验证规则
        Map<String, Object> cacheValidationMap = new HashMap<>();
        cacheValidationMap.put("allowedValues", Arrays.asList("REDIS", "MEMCACHED", "EHCACHE"));
        cacheValidationMap.put("errorMessage", "缓存类型必须是支持的类型之一");

        // 缓存配置的值定义
        Map<String, Object> cacheValueMap = new HashMap<>();
        cacheValueMap.put("defaultValue", "REDIS");
        cacheValueMap.put("required", false);
        cacheValueMap.put("validation", cacheValidationMap);

        // 缓存配置的元数据
        Map<String, Object> cacheMetaDataMap = new HashMap<>();
        cacheMetaDataMap.put("name", "缓存引擎类型");
        cacheMetaDataMap.put("type", "ENUM");
        cacheMetaDataMap.put("desc", "指定应用程序使用的缓存引擎类型");

        // 完整的缓存配置
        Map<String, Object> cacheConfigMap = new HashMap<>();
        cacheConfigMap.put("configId", "CACHE_ENGINE");
        cacheConfigMap.put("environment", "PRODUCTION");
        cacheConfigMap.put("category", "CACHE");
        cacheConfigMap.put("metadata", cacheMetaDataMap);
        cacheConfigMap.put("value", cacheValueMap);

        // 2. 使用TML文档框架包装配置数据
        Config dbConfig = new Config(dbConfigMap);
        Config cacheConfig = new Config(cacheConfigMap);

        System.out.println("✓ 配置数据构建完成\n");

        // 3. 演示配置读取和应用
        System.out.println("--- 配置信息读取与验证 ---");

        // 处理数据库配置
        processConfiguration("数据库连接池", dbConfig);
        
        System.out.println();
        
        // 处理缓存配置
        processConfiguration("缓存引擎", cacheConfig);

        // 4. 演示动态配置管理
        System.out.println("\n--- 动态配置管理演示 ---");
        
        // 模拟配置热更新
        System.out.println("模拟配置热更新...");
        dbConfig.put("lastModified", System.currentTimeMillis());
        dbConfig.put("modifiedBy", "system-admin");
        
        // 读取更新后的动态属性
        System.out.println("更新时间: " + dbConfig.get("lastModified"));
        System.out.println("更新人员: " + dbConfig.get("modifiedBy"));

        // 5. 演示配置验证流程
        System.out.println("\n--- 配置验证流程演示 ---");
        validateConfigurationValue(dbConfig, 50);  // 有效值
        validateConfigurationValue(dbConfig, 150); // 无效值

        System.out.println("\n=== 演示完成 ===");
    }

    /**
     * 处理单个配置项的完整流程
     */
    private static void processConfiguration(String configName, Config config) {
        System.out.println("🔧 处理配置: " + configName);
        
        // 读取基本信息
        String configId = (String) config.get("configId");
        String environment = (String) config.get("environment");
        String category = (String) config.get("category");
        
        System.out.println("  配置ID: " + configId);
        System.out.println("  环境: " + environment);
        System.out.println("  分类: " + category);

        // 读取元数据信息
        config.getMetaData().ifPresent(metadata -> {
            System.out.println("  元数据信息:");
            metadata.getName().ifPresent(name -> 
                System.out.println("    名称: " + name));
            metadata.getTime().ifPresent(type -> 
                System.out.println("    类型: " + type));
            metadata.getDesc().ifPresent(desc -> 
                System.out.println("    描述: " + desc));
        });

        // 读取值配置信息
        config.getValue().ifPresent(value -> {
            System.out.println("  值配置:");
            value.getDefaultValue().ifPresent(defaultVal -> 
                System.out.println("    默认值: " + defaultVal));
            value.getRequired().ifPresent(required -> 
                System.out.println("    是否必需: " + (required ? "是" : "否")));
            
            // 读取验证规则
            value.getValidation().ifPresent(validation -> {
                System.out.println("    验证规则:");
                Object minValue = validation.get("minValue");
                Object maxValue = validation.get("maxValue");
                Object allowedValues = validation.get("allowedValues");
                Object errorMessage = validation.get("errorMessage");
                
                if (minValue != null && maxValue != null) {
                    System.out.println("      数值范围: " + minValue + " - " + maxValue);
                }
                if (allowedValues != null) {
                    System.out.println("      允许值: " + allowedValues);
                }
                if (errorMessage != null) {
                    System.out.println("      错误信息: " + errorMessage);
                }
            });
        });
    }

    /**
     * 演示配置值验证逻辑
     */
    private static void validateConfigurationValue(Config config, Object testValue) {
        System.out.println("验证配置值: " + testValue);
        
        config.getValue().ifPresent(value -> {
            value.getValidation().ifPresent(validation -> {
                Object minValue = validation.get("minValue");
                Object maxValue = validation.get("maxValue");
                Object errorMessage = validation.get("errorMessage");
                
                if (minValue != null && maxValue != null && testValue instanceof Number) {
                    int val = ((Number) testValue).intValue();
                    int min = ((Number) minValue).intValue();
                    int max = ((Number) maxValue).intValue();
                    
                    if (val >= min && val <= max) {
                        System.out.println("  ✅ 验证通过: 值 " + val + " 在有效范围内");
                    } else {
                        System.out.println("  ❌ 验证失败: " + errorMessage);
                    }
                }
            });
        });
    }
}