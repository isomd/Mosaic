package io.github.tml.mosaic.component.enhance;

import io.github.tml.mosaic.install.domian.InstallationItem;
import io.github.tml.mosaic.install.enhance.AbstractInstallationConfigEnhancer;
import io.github.tml.mosaic.install.support.ResourceFileType;

/**
 * 描述: 自动注入本地代码的配置增强
 * @author suifeng
 * 日期: 2025/06/09
 */
//@Component
public class CodeInstallationItemEnhancer extends AbstractInstallationConfigEnhancer {

    @Override
    protected InstallationItem addInstallationItem() {
        return createInstallationItem(ResourceFileType.CODE, "classpath:");
    }
}
