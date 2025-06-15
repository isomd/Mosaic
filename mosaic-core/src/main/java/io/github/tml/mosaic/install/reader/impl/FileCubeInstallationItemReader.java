package io.github.tml.mosaic.install.reader.impl;

import io.github.tml.mosaic.core.execption.CubeException;
import io.github.tml.mosaic.core.factory.io.loader.DefaultResourceLoader;
import io.github.tml.mosaic.install.InstallationConfig;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.install.reader.AbstractCubeInstallationItemReader;
import io.github.tml.mosaic.install.reader.ReaderType;
import io.github.tml.mosaic.install.support.ResourceFileType;

/**
 * 描述: 通用文件读取器
 * @author suifeng
 * 日期: 2025/6/15
 */
public class FileCubeInstallationItemReader extends AbstractCubeInstallationItemReader {

    public FileCubeInstallationItemReader() {
        super(new DefaultResourceLoader());
    }

    @Override
    public ReaderType getReaderType() {
        return ReaderType.FILE;
    }

    @Override
    protected InstallationConfig doLoadCubeInstallationItem(String actualLocation) throws CubeException {
        InstallationConfig installationConfig = new InstallationConfig();
        InstallationItem installationItem = new InstallationItem();
        installationItem.setType(ResourceFileType.JAR);
        installationItem.setLocation(actualLocation);
        installationConfig.addInstallation(installationItem);
        return installationConfig;
    }
}