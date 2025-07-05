package io.github.tml.mosaic.config.mosaic;

import io.github.tml.mosaic.install.adpter.CodeResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.JarResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.DefaultResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.enhance.core.InstallationConfigEnhancer;
import io.github.tml.mosaic.install.installer.DefaultInfoContextInstaller;
import io.github.tml.mosaic.install.installer.core.InfoContextInstaller;
import io.github.tml.mosaic.install.reader.FileCubeInstallationItemReader;
import io.github.tml.mosaic.install.reader.JsonCubeInstallationItemReader;
import io.github.tml.mosaic.install.reader.LocalProjectInstallationItemReader;
import io.github.tml.mosaic.install.support.ResourceFileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class MosaicInstallerConfig {

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
        List<String> resourceList = new ArrayList<>(Arrays.asList(resourcePath));
        resourceList.add("[CODE]");
        DefaultInfoContextInstaller defaultInfoContextInstaller =
                new DefaultInfoContextInstaller(
                        List.of(new JsonCubeInstallationItemReader(), new LocalProjectInstallationItemReader(), new FileCubeInstallationItemReader())
                        , resourceFileAdapterRegistry
                        , resourceList.toArray(new String[]{})
                );
        defaultInfoContextInstaller.setInstallationConfigEnhancers(installationConfigEnhancers);
        return defaultInfoContextInstaller;
    }
}
