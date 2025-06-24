package io.github.tml.mosaic.cube.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.converter.CubeDefinitionConverter;
import io.github.tml.mosaic.converter.InfoContextConverter;
import io.github.tml.mosaic.cube.factory.ClassPathCubeContext;
import io.github.tml.mosaic.cube.factory.context.CubeContext;
import io.github.tml.mosaic.cube.factory.definition.CubeDefinition;
import io.github.tml.mosaic.install.domian.info.CubeInfo;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;

import java.util.List;

public class CubeConfig {
    public static CubeContext cubeContext(InfoContextInstaller infoContextInstaller) {
        ClassPathCubeContext context = new ClassPathCubeContext();

        // 初始化安装项Context 收集 -> List<CubeInfo>
        List<CubeInfo> cubeInfos = InfoContextConverter.convertInfoContextsToCubeInfoList(infoContextInstaller.installCubeInfoContext());

        // List<CubeInfo> -> List<CubeDefinition>
        List<CubeDefinition> cubeDefinitions = CubeDefinitionConverter.convertCubeInfoListToCubeDefinitionList(cubeInfos);

        // 注册进context容器
        context.registerAllCubeDefinition(cubeDefinitions);

        // 刷新容器
        context.refresh();

        return context;
    }

    /**
     * 槽管理器
     * @return 槽管理器
     */
    public static SlotManager slotManager(){
        GenericSlotManager manager = GenericSlotManager.manager();
        GoldenShovel.loadSlotManager(manager);
        return manager;
    }

    public static CubeActuatorProxy cubeActuatorProxy(CubeContext cubeContext, SlotManager slotManager){
        CubeActuatorProxy cubeActuatorProxy = new CubeActuatorProxy();
        cubeActuatorProxy.init(cubeContext, slotManager);
        GoldenShovel.loadCubeActuatorProxy(cubeActuatorProxy);
        return cubeActuatorProxy;
    }
}
