package io.github.tml.mosaic.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.factory.ClassPathCubeContext;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.factory.definition.CubeDefinitionConverter;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.install.install.InfoContextInstaller;
import io.github.tml.mosaic.install.support.info.InfoContext;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.List;

/**
 * mosaic框架初始化
 */
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

        // 初始化安装项Context
        List<InfoContext> infoContexts = new ArrayList<>();
        if (infoContextInstaller != null) {
            infoContexts = infoContextInstaller.installCubeInfoContext();
        }

        // 将安装项转换成CubeDefinition列表
        List<CubeDefinition> cubeDefinitions = new ArrayList<>();
        if (infoContexts != null && !infoContexts.isEmpty()) {
            for (InfoContext infoContext : infoContexts) {
                cubeDefinitions.addAll(CubeDefinitionConverter.convertToCubeDefinitions(infoContext));
            }
        }

        // 注册进context容器
        for (CubeDefinition cubeDefinition : cubeDefinitions) {
            context.registerCubeDefinition(new GUUID(cubeDefinition.getId()), cubeDefinition);
        }

        return context;
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
