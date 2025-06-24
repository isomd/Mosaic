package io.github.tml.mosaic.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.cube.config.CubeConfig;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import io.github.tml.mosaic.world.construct.MosaicWorld;
import io.github.tml.mosaic.world.replace.ComponentReplace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

/**
 * mosaic框架初始化
 */
@Slf4j
@Configuration
public class MosaicInitConfig {
    /*
    * 当前世界
    * */
    @Bean
    @DependsOn({"infoContextInstaller", "cubeEventBroadcaster"})
    public MosaicWorld mosaicWorld(List<ComponentReplace> replaceList, InfoContextInstaller infoContextInstaller){
        return new MosaicWorld(replaceList, infoContextInstaller);
    }
    /**
     * cube上下文容器
     */
    @Bean
    @DependsOn({"infoContextInstaller", "cubeEventBroadcaster"})
    public CubeContext cubeContext(MosaicWorld mosaicWorld) {
        return (CubeContext) mosaicWorld.getWorldComponentManager().get("cubeContext");
    }

    /**
     * 槽管理器
     * @return 槽管理器
     */
    @Bean
    public SlotManager slotManager(MosaicWorld mosaicWorld){
        return (SlotManager) mosaicWorld.getWorldComponentManager().get("slotManager");
    }

    @Bean
    @DependsOn({"cubeContext", "slotManager"})
    public CubeActuatorProxy cubeActuatorProxy(CubeContext cubeContext, SlotManager slotManager){
        return CubeConfig.cubeActuatorProxy(cubeContext, slotManager);
    }
}