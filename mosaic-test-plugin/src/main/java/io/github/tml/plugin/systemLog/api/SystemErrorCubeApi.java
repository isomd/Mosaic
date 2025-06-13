package io.github.tml.plugin.systemLog.api;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.ExtensionPackage;
import io.github.tml.mosaic.cube.MExtension;
import io.github.tml.mosaic.cube.MExtensionPackage;
import io.github.tml.plugin.systemLog.cube.PropertySystemLogCube;
import io.github.tml.plugin.systemLog.cube.SingletonSystemLogCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID_P;
import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID_S;

@MExtensionPackage(value = "system.error",
        name = "系统日志错误输出包",
        version = "1.0.1",
        cubeId = PLUGIN_ID_P)
public class SystemErrorCubeApi extends ExtensionPackage<PropertySystemLogCube> {

    public SystemErrorCubeApi(PropertySystemLogCube cube, GUID guid) {
        super(cube, guid);
    }

    @MExtension(value = "error", name = "错误输出", priority = 1)
    public void error(String msg) {
        cube.error(msg);
    }

}
