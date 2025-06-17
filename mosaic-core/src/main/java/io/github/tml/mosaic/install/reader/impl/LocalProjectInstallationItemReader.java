package io.github.tml.mosaic.install.reader.impl;
import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.cube.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.install.InstallationConfig;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.install.reader.AbstractCubeInstallationItemReader;
import io.github.tml.mosaic.install.reader.ReaderType;
import io.github.tml.mosaic.install.support.ResourceFileType;

/**
 * 描述: 本地代码项目读取器
 * @author suifeng
 * 日期: 2025/6/15
 */
public class LocalProjectInstallationItemReader extends AbstractCubeInstallationItemReader {

    public LocalProjectInstallationItemReader() {
        super(new DefaultResourceLoader());
    }

    @Override
    public ReaderType getReaderType() {
        return ReaderType.CODE;
    }

    @Override
    protected InstallationConfig doLoadCubeInstallationItem(String actualLocation) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        InstallationItem installationItem = new InstallationItem();
        installationItem.setType(ResourceFileType.CODE);
        installationConfig.addInstallation(installationItem);
        return installationConfig;
    }
}
