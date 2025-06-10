package io.github.tml.mosaic.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.factory.ClassPathCubeContext;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.core.factory.definition.CubeDefinitionConverter;
import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.core.tools.guid.GUUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.install.reader.JsonCubeInstallationItemReader;
import io.github.tml.mosaic.core.factory.definition.CubeDefinition;
import io.github.tml.mosaic.install.adpter.CodeResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.JarResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.DefaultResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.enhance.InstallationConfigEnhancer;
import io.github.tml.mosaic.install.install.InfoContextInstaller;
import io.github.tml.mosaic.install.install.DefaultInfoContextInstaller;
import io.github.tml.mosaic.install.reader.LocalProjectInstallationItemReader;
import io.github.tml.mosaic.install.support.InfoContext;
import io.github.tml.mosaic.install.support.ResourceFileType;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * mosaic框架初始化
 */
@Configuration
public class MosaicInitConfig {

    /**
     * 启动资源路径
     */
    @Value("${mosaic.startup.resource-path:[]}")
    private String[] resourcePath;

    /**
     * 启动资源文件适配器
     */
    @Bean
    public ResourceFileAdapterRegistry resourceFileAdapterRegistry() {
        DefaultResourceFileAdapterRegistry defaultResourceFileAdapterRegistry = new DefaultResourceFileAdapterRegistry();
        defaultResourceFileAdapterRegistry.registerAdapter(ResourceFileType.JAR, new JarResourceFileAdapter());
        defaultResourceFileAdapterRegistry.registerAdapter(ResourceFileType.CODE, new CodeResourceFileAdapter());
        return defaultResourceFileAdapterRegistry;
    }

    /**
     * InfoContext安装器
     */
    @Bean
    public InfoContextInstaller infoContextInstaller(ResourceFileAdapterRegistry resourceFileAdapterRegistry, List<InstallationConfigEnhancer> installationConfigEnhancers) {
        DefaultInfoContextInstaller defaultInfoContextInstaller =
                new DefaultInfoContextInstaller(
                        List.of(new JsonCubeInstallationItemReader())
                        , resourceFileAdapterRegistry);
        defaultInfoContextInstaller.setInstallationConfigEnhancers(installationConfigEnhancers);
        return defaultInfoContextInstaller;
    }

    /**
     * cube上下文容器
     * @param infoContextInstaller 启动资源文件适配器
     */
    @Bean
    @DependsOn("infoContextInstaller")
    public CubeContext cubeContext(InfoContextInstaller infoContextInstaller) {
        ClassPathCubeContext context = new ClassPathCubeContext(resourcePath);

        List<InfoContext> infoContexts = new ArrayList<>();
        if (infoContextInstaller != null) {
            infoContexts = infoContextInstaller.installCubeInfoContext(context.getConfigLocations());
        }

        List<CubeDefinition> cubeDefinitions = new ArrayList<>();
        if (infoContexts != null && !infoContexts.isEmpty()) {
            for (InfoContext infoContext : infoContexts) {
                cubeDefinitions.addAll(CubeDefinitionConverter.convertToCubeDefinitions(infoContext));
            }
        }

        for (CubeDefinition cubeDefinition : cubeDefinitions) {
            context.registerCubeDefinition(cubeDefinition.getId(), cubeDefinition);
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
