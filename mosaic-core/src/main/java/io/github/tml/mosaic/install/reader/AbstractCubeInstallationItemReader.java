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
 * Cube定义读取抽象类
 * 基于ReaderType枚举进行前缀路由
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

    /**
     * 子类实现，返回支持的Reader类型
     */
    public abstract ReaderType getReaderType();

    @Override
    public InstallationConfig loadCubeInstallationItem(String location) throws CubeException {
        // 检查前缀是否匹配当前Reader类型
        ReaderType readerType = getReaderType();
        if (!readerType.matches(location)) {
            return new InstallationConfig(); // 不匹配返回空配置
        }

        // 去掉前缀，交给子类处理
        String actualLocation = readerType.removePrefix(location);
        return doLoadCubeInstallationItem(actualLocation);
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(Resource resource) throws CubeException {
        return doLoadCubeInstallationItem(resource);
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(Resource... resources) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        List<InstallationItem> installations = new ArrayList<>();
        for (Resource resource : resources) {
            InstallationConfig temp = loadCubeInstallationItem(resource);
            if (temp != null && temp.getInstallations() != null) {
                installations.addAll(temp.getInstallations());
            }
        }
        installationConfig.setInstallations(installations);
        return installationConfig;
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(String... locations) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        List<InstallationItem> installations = new ArrayList<>();
        for (String location : locations) {
            InstallationConfig temp = loadCubeInstallationItem(location);
            if (temp != null && temp.getInstallations() != null) {
                installations.addAll(temp.getInstallations());
            }
        }
        installationConfig.setInstallations(installations);
        return installationConfig;
    }

    /**
     * 子类实现具体的加载逻辑（基于location）
     */
    protected InstallationConfig doLoadCubeInstallationItem(String actualLocation) throws CubeException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(actualLocation);
        return doLoadCubeInstallationItem(resource);
    }

    /**
     * 子类实现具体的加载逻辑（基于Resource）
     */
    protected InstallationConfig doLoadCubeInstallationItem(Resource resource) throws CubeException {
        return new InstallationConfig();
    }
}