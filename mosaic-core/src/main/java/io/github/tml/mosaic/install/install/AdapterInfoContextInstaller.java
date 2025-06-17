package io.github.tml.mosaic.install.install;

import io.github.tml.mosaic.install.domian.InstallationConfig;
import io.github.tml.mosaic.install.domian.InstallationItem;
import io.github.tml.mosaic.install.reader.CubeInstallationItemReader;
import io.github.tml.mosaic.install.adpter.core.ResourceFileAdapter;
import io.github.tml.mosaic.install.adpter.registry.ResourceFileAdapterRegistry;
import io.github.tml.mosaic.install.domian.InfoContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * @author suifeng
 * 日期: 2025/6/9
 */
@Slf4j
public abstract class AdapterInfoContextInstaller extends ResourceInfoContextInstaller {

    protected ResourceFileAdapterRegistry adapterRegistry;

    public AdapterInfoContextInstaller(List<CubeInstallationItemReader> cubeInstallationItemReaderList, ResourceFileAdapterRegistry adapterRegistry) {
        super(cubeInstallationItemReaderList);
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    protected List<InfoContext> convertInstallationConfigToInfoContexts(InstallationConfig installationConfig) {
        List<InfoContext> infoContexts = new ArrayList<InfoContext>();

        if (installationConfig != null) {
            for (InstallationItem item : installationConfig.getInstallations()) {
                ResourceFileAdapter adapter = adapterRegistry.getAdapter(item.getType());
                if (adapter == null) {
                    log.error("Adapter not found for type {}", item.getType());
                    continue;
                }
                InfoContext infoContext = adapter.adapter(item);
                infoContexts.add(infoContext);
            }
        }

        return infoContexts;
    }
}
