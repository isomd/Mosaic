package io.github.tml.mosaic.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.converter.InfoContextConverter;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.converter.CubeDefinitionConverter;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.core.tools.guid.DotNotationId;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.install.domian.InfoContext;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.cube.Cube;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mosaic框架初始化
 */
@Slf4j
@Configuration
public class MosaicInitConfig {

    /**
     * cube上下文容器
     * @param infoContextInstaller 启动资源文件适配器
     */
    @Bean
    @DependsOn({"infoContextInstaller", "cubeEventBroadcaster"})
    public CubeContext cubeContext(InfoContextInstaller infoContextInstaller) {
        ClassPathCubeContext context = new ClassPathCubeContext();

        // 初始化安装项Context 收集 -> List<CubeInfo>
        List<CubeInfo> cubeInfos = InfoContextConverter.convertInfoContextsToCubeInfoList(infoContextInstaller.installCubeInfoContext());

        // List<CubeInfo> -> List<CubeDefinition>
        List<CubeDefinition> cubeDefinitions = CubeDefinitionConverter.convertCubeInfoListToCubeDefinitionList(cubeInfos);

        // 注册进context容器
        context.registerAllCubeDefinition(cubeDefinitions);

        // 执行配置测试
        testCubeConfigurations(context);

        return context;
    }

    /**
     * 测试Cube配置注入和校验
     */
    private void testCubeConfigurations(CubeContext context) {
        log.info("========== 开始Cube配置测试 ==========");

        // 测试正确配置
        testValidConfigurations(context);

        // 测试错误配置
        testInvalidConfigurations(context);

        log.info("========== Cube配置测试完成 ==========");
    }

    /**
     * 测试有效配置（应该通过校验）
     */
    private void testValidConfigurations(CubeContext context) {
        log.info("---------- 测试有效配置 ----------");

        // 测试案例1：完整配置
        testCase("完整配置", context, createValidCompleteConfig());

        // 测试案例2：最小必填配置
        testCase("最小必填配置", context, createValidMinimalConfig());

        // 测试案例3：部分配置（依赖默认值）
        testCase("部分配置", context, createValidPartialConfig());

        // 测试案例4：边界值配置
        testCase("边界值配置", context, createValidBoundaryConfig());
    }

    /**
     * 测试无效配置（应该抛出异常）
     */
    private void testInvalidConfigurations(CubeContext context) {
        log.info("---------- 测试无效配置 ----------");

        // 测试案例1：缺少必填项
        testInvalidCase("缺少必填项", context, createInvalidMissingRequiredConfig());

        // 测试案例2：类型错误
        testInvalidCase("类型错误", context, createInvalidTypeConfig());

        // 测试案例3：枚举值错误
        testInvalidCase("枚举值错误", context, createInvalidEnumConfig());

        // 测试案例4：空值配置
        testInvalidCase("空值配置", context, createInvalidEmptyConfig());
    }

    /**
     * 执行有效配置测试
     */
    private void testCase(String caseName, CubeContext context, Map<String, Object> config) {
        try {
            Cube cube = context.getCube(new DotNotationId("system.log.cube.p"), config);
            if (cube != null) {
                log.info("✓ {} - 测试通过", caseName);
                logCubeConfig(cube, config);
            } else {
                log.error("✗ {} - Cube创建失败", caseName);
            }
        } catch (Exception e) {
            log.error("✗ {} - 测试失败: {}", caseName, e.getMessage());
        }
    }

    /**
     * 执行无效配置测试
     */
    private void testInvalidCase(String caseName, CubeContext context, Map<String, Object> config) {
        try {
            Cube cube = context.getCube(new DotNotationId("system.log.cube.p"), config);
            log.error("✗ {} - 应该失败但成功了", caseName);
        } catch (Exception e) {
            log.info("✓ {} - 正确抛出异常: {}", caseName, e.getMessage());
        }
    }

    /**
     * 打印Cube配置信息
     */
    private void logCubeConfig(Cube cube, Map<String, Object> inputConfig) {
        log.info("  输入配置: {}", inputConfig);
        log.info("  实际配置: {}", cube.getAllConfigs());

        // 验证配置值
        String outputFormat = cube.getConfig("outputFormat", String.class);
        Integer retentionDays = cube.getConfig("retentionDays", Integer.class);
        String logLevel = cube.getConfig("logLevel", String.class);
        Integer maxFileSize = cube.getConfig("maxFileSize", Integer.class);
        Boolean enableConsoleOutput = cube.getConfig("enableConsoleOutput", Boolean.class);

        log.info("  解析结果: outputFormat={}, retentionDays={}, logLevel={}, maxFileSize={}, enableConsoleOutput={}",
                outputFormat, retentionDays, logLevel, maxFileSize, enableConsoleOutput);
    }

    // ========== 有效配置创建方法 ==========

    /**
     * 创建完整有效配置
     */
    private Map<String, Object> createValidCompleteConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", "[%date] %level %logger - %message");
        config.put("retentionDays", 60);
        config.put("logLevel", "DEBUG");
        config.put("maxFileSize", 20971520); // 20MB
        config.put("enableConsoleOutput", false);
        return config;
    }

    /**
     * 创建最小必填配置
     */
    private Map<String, Object> createValidMinimalConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", "%message");
        config.put("logLevel", "INFO");
        // 其他非必填项将使用默认值
        return config;
    }

    /**
     * 创建部分配置
     */
    private Map<String, Object> createValidPartialConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", "[%date] %message");
        config.put("logLevel", "WARN");
        config.put("retentionDays", 7);
        // maxFileSize和enableConsoleOutput使用默认值
        return config;
    }

    /**
     * 创建边界值配置
     */
    private Map<String, Object> createValidBoundaryConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", "");  // 空字符串但不是null
        config.put("retentionDays", 1);  // 最小值
        config.put("logLevel", "ERROR"); // 枚举边界值
        config.put("maxFileSize", 1024); // 小文件
        config.put("enableConsoleOutput", true);
        return config;
    }

    // ========== 无效配置创建方法 ==========

    /**
     * 创建缺少必填项的配置
     */
    private Map<String, Object> createInvalidMissingRequiredConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("retentionDays", 30);
        config.put("enableConsoleOutput", true);
        // 缺少必填项: outputFormat, logLevel
        return config;
    }

    /**
     * 创建类型错误的配置
     */
    private Map<String, Object> createInvalidTypeConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", "[%date] %message");
        config.put("retentionDays", "not_a_number"); // 应该是Integer
        config.put("logLevel", "INFO");
        config.put("maxFileSize", "invalid_size");   // 应该是Integer
        config.put("enableConsoleOutput", "not_boolean"); // 应该是Boolean
        return config;
    }

    /**
     * 创建枚举值错误的配置
     */
    private Map<String, Object> createInvalidEnumConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", "[%date] %message");
        config.put("retentionDays", 30);
        config.put("logLevel", "INVALID_LEVEL"); // 不在允许的枚举值中
        config.put("maxFileSize", 10485760);
        config.put("enableConsoleOutput", true);
        return config;
    }

    /**
     * 创建空值配置
     */
    private Map<String, Object> createInvalidEmptyConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("outputFormat", null); // 必填项为null
        config.put("retentionDays", 30);
        config.put("logLevel", null);     // 必填项为null
        config.put("maxFileSize", 10485760);
        config.put("enableConsoleOutput", true);
        return config;
    }

    /**
     * 槽管理器
     * @return 槽管理器
     */
    @Bean
    public SlotManager slotManager(){
        GenericSlotManager manager = GenericSlotManager.manager();
        GoldenShovel.loadSlotManager(manager);
        return manager;
    }

    @Bean
    @DependsOn({"cubeContext", "slotManager"})
    public CubeActuatorProxy cubeActuatorProxy(CubeContext cubeContext, SlotManager slotManager){
        CubeActuatorProxy cubeActuatorProxy = new CubeActuatorProxy();
        cubeActuatorProxy.init(cubeContext, slotManager);
        GoldenShovel.loadCubeActuatorProxy(cubeActuatorProxy);
        return cubeActuatorProxy;
    }
}