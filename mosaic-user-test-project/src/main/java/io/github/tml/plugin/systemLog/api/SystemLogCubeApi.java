package io.github.tml.plugin.systemLog.api;

import io.github.tml.mosaic.cube.external.MExtension;
import io.github.tml.mosaic.cube.external.MExtensionPackage;
import io.github.tml.mosaic.cube.external.MosaicExtPackage;
import io.github.tml.plugin.systemLog.cube.SystemLogCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID;


@MExtensionPackage(
        name = "系统日志输出扩展包",
        description = "用于系统日志输出",
        cubeId = PLUGIN_ID)
public class SystemLogCubeApi extends MosaicExtPackage<SystemLogCube> {

    @MExtension(extPointId = "log", name = "日志输出", description = "输出日志", priority = 1)
    public void log(String msg) {
        mosaicCube.log(msg);
    }

    @Override
    public String extPackageId() {
        return "system.log";
    }
}
