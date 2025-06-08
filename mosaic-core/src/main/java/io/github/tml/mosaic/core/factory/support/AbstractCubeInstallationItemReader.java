package io.github.tml.mosaic.core.factory.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.config.CubeDefinitionRegistry;
import io.github.tml.mosaic.core.factory.context.json.InstallationConfig;
import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;

/**
 * 描述: Cube定义读取抽象类
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractCubeInstallationItemReader implements CubeInstallationItemReader {

    private final CubeDefinitionRegistry registry;

    private final ResourceLoader resourceLoader;

    protected AbstractCubeInstallationItemReader(CubeDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }

    public AbstractCubeInstallationItemReader(CubeDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public CubeDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(Resource... resources) throws CubeException {
        for (Resource resource : resources) {
            loadCubeInstallationItem(resource);
        }
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(String location) throws CubeException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        loadCubeInstallationItem(resource);
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(String... locations) throws CubeException {
        for (String location : locations) {
            loadCubeInstallationItem(location);
        }
    }
}
