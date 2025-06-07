package io.github.tml.mosaic.install.support.registry;

import io.github.tml.mosaic.install.CubeInstaller;

public interface InstallerRegistry {
    void registerInstaller(String installerType, CubeInstaller installer);
    CubeInstaller getInstaller(String installerType);
}