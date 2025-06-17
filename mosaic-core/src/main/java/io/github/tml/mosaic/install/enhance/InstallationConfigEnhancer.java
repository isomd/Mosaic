package io.github.tml.mosaic.install.enhance;

import io.github.tml.mosaic.install.domian.InstallationConfig;

/**
 * 描述: InstallationConfig的后置增强处理
 * @author suifeng
 * 日期: 2025/06/09
 */
public interface InstallationConfigEnhancer {

    void enhance(InstallationConfig installationConfig);
}
