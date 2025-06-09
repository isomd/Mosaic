package io.github.tml.mosaic.config;

import io.github.tml.mosaic.GoldenShovel;
import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.factory.ClassPathJsonCubeContext;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.install.adpter.CodeResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.JarResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.DefaultResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.enhance.InstallationConfigEnhancer;
import io.github.tml.mosaic.install.install.CubeDefinitionInstaller;
import io.github.tml.mosaic.install.install.DefaultCubeDefinitionInstaller;
import io.github.tml.mosaic.install.support.ResourceFileType;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import java.util.List;

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
    public ResourceFileAdapterRegistry resourceFileAdapterRegistry(){
        DefaultResourceFileAdapterRegistry defaultResourceFileAdapterRegistry = new DefaultResourceFileAdapterRegistry();
        defaultResourceFileAdapterRegistry.registerAdapter(ResourceFileType.JAR, new JarResourceFileAdapter());
        defaultResourceFileAdapterRegistry.registerAdapter(ResourceFileType.CODE, new CodeResourceFileAdapter());
        return defaultResourceFileAdapterRegistry;
    }

    /**
     * 启动安装器
     */
    @Bean
    public CubeDefinitionInstaller cubeDefinitionInstaller(){
        return new DefaultCubeDefinitionInstaller();
    }

    /**
     * cube上下文容器
     * @param resourceFileAdapterRegistry 启动资源文件适配器
     */
    @Bean
    @DependsOn("resourceFileAdapterRegistry")
    public CubeContext cubeContext(ResourceFileAdapterRegistry resourceFileAdapterRegistry, CubeDefinitionInstaller cubeDefinitionInstaller, List<InstallationConfigEnhancer> installationConfigEnhancers) {
        ClassPathJsonCubeContext classPathJsonCubeContext = null;

        if (resourcePath != null && resourcePath.length > 0){
            classPathJsonCubeContext = new ClassPathJsonCubeContext(resourcePath);
        } else {
            classPathJsonCubeContext = new ClassPathJsonCubeContext();
        }

        if (cubeDefinitionInstaller != null){
            classPathJsonCubeContext.setCubeDefinitionInstaller(cubeDefinitionInstaller);
        }

        if (resourceFileAdapterRegistry != null){
            classPathJsonCubeContext.setResourceFileAdapterRegistry(resourceFileAdapterRegistry);
        }

        if (installationConfigEnhancers != null){
            classPathJsonCubeContext.setInstallationConfigEnhancers(installationConfigEnhancers);
        }

        // 在此处刷新
        classPathJsonCubeContext.refresh();

        return classPathJsonCubeContext;
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
