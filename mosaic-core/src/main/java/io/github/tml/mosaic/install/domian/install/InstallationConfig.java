package io.github.tml.mosaic.install.domian.install;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述: 启动配置类
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
public class InstallationConfig {

    private List<InstallationItem> installations = new ArrayList<InstallationItem>();

    public void addInstallation(InstallationItem installation) {
        installations.add(installation);
    }
}
