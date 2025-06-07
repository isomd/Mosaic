package io.github.tml.mosaic.install.support.registry;

import io.github.tml.mosaic.install.CubeInstaller;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultInstallerRegistry implements InstallerRegistry {

    private final Map<String, CubeInstaller> installerMap = new ConcurrentHashMap<>();

    @Override
    public void registerInstaller(String installerType, CubeInstaller installer) {
        installerMap.put(installerType, installer);
    }

    @Override
    public CubeInstaller getInstaller(String installerType) {
        return installerMap.get(installerType);
    }
}