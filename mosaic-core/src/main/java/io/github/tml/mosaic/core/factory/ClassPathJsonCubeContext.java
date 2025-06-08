package io.github.tml.mosaic.core.factory;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.context.json.JsonCubeInstallationItemReader;
import io.github.tml.mosaic.core.factory.context.support.AbstractResourcesLoaderCubeContext;
import io.github.tml.mosaic.core.factory.support.CubeInstallationItemReader;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.install.CubeDefinitionInstaller;
import lombok.Data;


/**
 * 描述: 核心应用上下文实现类
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
public class ClassPathJsonCubeContext extends AbstractResourcesLoaderCubeContext {

    private ResourceFileAdapterRegistry adapterRegistry;

    private CubeDefinitionInstaller cubeDefinitionInstaller;

    private CubeInstallationItemReader cubeInstallationItemReader = new JsonCubeInstallationItemReader();

    private String[] configLocations;

    public ClassPathJsonCubeContext() {
    }

    /**
     * 从 JSON 中加载 CubeDefinition，并刷新上下文
     */
    public ClassPathJsonCubeContext(String configLocations) throws CubeException {
        this(new String[]{configLocations});
    }

    public ClassPathJsonCubeContext(String[] configLocations) throws CubeException {
        super();
        this.configLocations = configLocations;
    }

    public void setCubeDefinitionInstaller(CubeDefinitionInstaller cubeDefinitionInstaller) {
        this.cubeDefinitionInstaller = cubeDefinitionInstaller;
        cubeDefinitionInstaller.setRegistry(getBeanFactory());
    }

    public void setResourceFileAdapterRegistry(ResourceFileAdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }

    @Override
    protected ResourceFileAdapterRegistry getAdapterRegistry() {
        return adapterRegistry;
    }

    @Override
    protected CubeDefinitionInstaller getCubeDefinitionInstaller() {
        return cubeDefinitionInstaller;
    }
}
