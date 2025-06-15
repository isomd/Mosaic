package io.github.tml.plugin.systemLog.api;

import io.github.tml.mosaic.cube.MExtension;
import io.github.tml.mosaic.cube.MExtensionPackage;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.plugin.systemLog.cube.SystemLogCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID;


@MExtensionPackage(value = "system.log",
        name = "系统日志输出扩展包",
        description = "用于系统日志输出",
        version = "1.0.1",
        cubeId = PLUGIN_ID)
public class SystemLogCubeApi extends MosaicExtPackage<SystemLogCube> {

    @MExtension(value = "log", name = "日志输出", description = "输出日志", priority = 1)
    public void log(String msg) {
        mosaicCube.log(msg);
    }

    @Override
    public String extPackageId() {
        return "system.log";
    }
}
