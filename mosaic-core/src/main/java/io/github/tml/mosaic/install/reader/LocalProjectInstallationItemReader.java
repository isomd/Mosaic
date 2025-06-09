package io.github.tml.mosaic.install.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.core.factory.io.loader.ResourceLoader;
import io.github.tml.mosaic.core.factory.io.resource.Resource;
import io.github.tml.mosaic.install.InstallationConfig;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.install.support.ResourceFileType;

/**
 * 描述: 从Json的配置文件中去加载CubeInfo
 * @author suifeng
 * 日期: 2025/6/7
 */
public class LocalProjectInstallationItemReader extends AbstractCubeInstallationItemReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LocalProjectInstallationItemReader() {
        super(new DefaultResourceLoader());
    }

    public LocalProjectInstallationItemReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public InstallationConfig loadCubeInstallationItem(Resource resource) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        InstallationItem installationItem = new InstallationItem();
        installationItem.setType(ResourceFileType.CODE);
        installationConfig.addInstallation(installationItem);
        return installationConfig;
    }
}
