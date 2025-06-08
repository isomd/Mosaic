package io.github.tml.mosaic.config;

import io.github.tml.mosaic.actuator.CubeActuatorProxy;
import io.github.tml.mosaic.core.factory.ClassPathJsonCubeContext;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.install.adpter.JarResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.DefaultResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.support.ResourceFileType;
import io.github.tml.mosaic.slot.infrastructure.GenericSlotManager;
import io.github.tml.mosaic.slot.infrastructure.SlotManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
        return defaultResourceFileAdapterRegistry;
    }


    /**
     * cube上下文容器
     * @param resourceFileAdapterRegistry 启动资源文件适配器
     * @return
     */
    @Bean
    @DependsOn("resourceFileAdapterRegistry")
    public CubeContext cubeContext(ResourceFileAdapterRegistry resourceFileAdapterRegistry) {

        if(resourcePath != null && resourcePath.length > 0){
            return new ClassPathJsonCubeContext(resourcePath, resourceFileAdapterRegistry);
        }

       return new ClassPathJsonCubeContext();
    }

    /**
     * 槽管理器
     * @return 槽管理器
     */
    @Bean
    public SlotManager slotManager(){
        return GenericSlotManager.manager();
    }

    @Bean
    @DependsOn({"cubeContext", "slotManager"})
    public CubeActuatorProxy cubeActuatorProxy(CubeContext cubeContext, SlotManager slotManager){
        CubeActuatorProxy cubeActuatorProxy = new CubeActuatorProxy();
        cubeActuatorProxy.init(cubeContext, slotManager);
        return cubeActuatorProxy;
    }
}
