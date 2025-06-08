package io.github.tml.mosaic.core.factory.context.support;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.context.json.InstallationConfig;
import io.github.tml.mosaic.core.factory.context.json.InstallationItem;
import io.github.tml.mosaic.core.factory.support.CubeInstallationItemReader;
import io.github.tml.mosaic.core.factory.support.ListableCubeFactory;
import io.github.tml.mosaic.install.adpter.core.ResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.install.CubeDefinitionInstaller;
import io.github.tml.mosaic.install.support.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 上下文中对配置信息的加载
 *
 * @author suifeng
 * 日期: 2025/6/7
 */
@Slf4j
public abstract class AbstractResourcesLoaderCubeContext extends AbstractRefreshableCubeContext {

    protected ResourceFileAdapterRegistry adapterRegistry;

    protected CubeDefinitionInstaller cubeDefinitionInstaller;

    protected CubeInstallationItemReader cubeInstallationItemReader;

    protected List<InstallationItem> installationItems = new ArrayList<>();

    @Override
    protected void loadCubeDefinitions(ListableCubeFactory cubeFactory) {
        // TODO 加载必备项
        installationItems.forEach(this::processInstallationItem);
        String[] configLocations = getConfigLocations();
        if (null != configLocations) {
            InstallationConfig installationConfig = cubeInstallationItemReader.loadCubeInstallationItem(configLocations);
            if (null != installationConfig && !installationConfig.getInstallations().isEmpty()) {
                installationConfig.getInstallations().forEach(this::processInstallationItem);
            } else {
                log.warn("您暂未填写配置项，将为您构造一个空的context");
            }
        }
    }

    private void processInstallationItem(InstallationItem item) throws CubeException {
        ResourceFileAdapter adapter = getAdapterRegistry().getAdapter(item.getType());
        if (adapter == null) {
            throw new CubeException("No adapter for type: " + item.getType());
        }
        InfoContext infoContext = adapter.adapter(item);
        getCubeDefinitionInstaller().installCubeDefinition(infoContext);
    }

    public void setCubeDefinitionInstaller(CubeDefinitionInstaller cubeDefinitionInstaller) {
        this.cubeDefinitionInstaller = cubeDefinitionInstaller;
        cubeDefinitionInstaller.setRegistry(getBeanFactory());
    }


    public void setResourceFileAdapterRegistry(ResourceFileAdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    public void addInstallationItem(List<InstallationItem> items) {
        installationItems.addAll(items);
    }

    protected ResourceFileAdapterRegistry getAdapterRegistry() {
        return adapterRegistry;
    }

    protected CubeDefinitionInstaller getCubeDefinitionInstaller() {
        return cubeDefinitionInstaller;
    }

    protected abstract String[] getConfigLocations();
}
