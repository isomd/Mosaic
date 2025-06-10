package io.github.tml.plugin.systemLog.cube;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.MCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID_P;

@MCube(value = PLUGIN_ID_P,
        name="系统日志（多例测试）",
        description = "用于输出系统日志的插件",
        version = "1.0.1", model = "property")
public class PropertySystemLogCube extends Cube {

    public PropertySystemLogCube(GUID id) {
        super(id);
    }

    public void log(String message){
        System.out.println(message);
    }

    public void error(String message){
        System.err.println(message);
    }
}
