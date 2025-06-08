package io.github.tml.mosaic.config;

import io.github.tml.mosaic.core.factory.ClassPathJsonCubeContext;
import io.github.tml.mosaic.core.factory.context.CubeContext;
import io.github.tml.mosaic.install.adpter.JarResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.DefaultResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.support.ResourceFileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;

import java.util.Objects;

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
     * 资源文件适配器
     */
    @Bean
    public ResourceFileAdapterRegistry resourceFileAdapterRegistry(){
        DefaultResourceFileAdapterRegistry defaultResourceFileAdapterRegistry = new DefaultResourceFileAdapterRegistry();
        defaultResourceFileAdapterRegistry.registerAdapter(ResourceFileType.JAR, new JarResourceFileAdapter());
        return defaultResourceFileAdapterRegistry;
    }


    @Bean
    @DependsOn("resourceFileAdapterRegistry")
    public CubeContext cubeContext(ResourceFileAdapterRegistry resourceFileAdapterRegistry) {

        if(resourcePath != null && resourcePath.length > 0){
            return new ClassPathJsonCubeContext(resourcePath, resourceFileAdapterRegistry);
        }

       return new ClassPathJsonCubeContext();
    }
}
