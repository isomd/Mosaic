package io.github.tml.mosaic.install.reader;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.install.InstallationConfig;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: Cube定义读取抽象类
 * @author suifeng
 * 日期: 2025/6/7
 */
public abstract class AbstractCubeInstallationItemReader implements CubeInstallationItemReader {

    private final ResourceLoader resourceLoader;

    protected AbstractCubeInstallationItemReader() {
        this(new DefaultResourceLoader());
    }

    public AbstractCubeInstallationItemReader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(Resource... resources) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        List<InstallationItem> installations = new ArrayList<InstallationItem>();
        for (Resource resource : resources) {
            InstallationConfig temp = loadCubeInstallationItem(resource);
            installations.addAll(temp.getInstallations());
        }
        installationConfig.setInstallations(installations);
        return installationConfig;
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(String location) throws CubeException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        return loadCubeInstallationItem(resource);
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(String... locations) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        List<InstallationItem> installations = new ArrayList<InstallationItem>();
        for (String location : locations) {
            InstallationConfig temp = loadCubeInstallationItem(location);
            installations.addAll(temp.getInstallations());
        }
        installationConfig.setInstallations(installations);
        return installationConfig;
    }
}
