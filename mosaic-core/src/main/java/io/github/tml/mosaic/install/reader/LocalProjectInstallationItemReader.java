package io.github.tml.mosaic.install.reader;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
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

    public LocalProjectInstallationItemReader() {
        super(new DefaultResourceLoader());
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
