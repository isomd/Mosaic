package io.github.tml.mosaic.component.enhance;

import io.github.tml.mosaic.core.factory.context.json.InstallationConfig;
import io.github.tml.mosaic.core.factory.context.json.InstallationItem;
import io.github.tml.mosaic.install.enhance.InstallationConfigEnhancer;
import io.github.tml.mosaic.install.support.ResourceFileType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述: 自动注入本地代码的配置增强
 * @author suifeng
 * 日期: 2025/06/09
 */
@Component
public class CodeInstallationItemEnhancer implements InstallationConfigEnhancer {

    @Override
    public void enhance(InstallationConfig installationConfig) {
        List<InstallationItem> installations = installationConfig.getInstallations();

        InstallationItem installationItem = new InstallationItem();
        installationItem.setType(ResourceFileType.CODE);
        installationItem.setPackageName("io.github.tml");
        installations.add(installationItem);

        installationConfig.setInstallations(installations);
    }
}
