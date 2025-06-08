package io.github.tml.systemLog.cube;

import io.github.tml.mosaic.core.tools.guid.GUID;
import io.github.tml.mosaic.cube.Cube;
import io.github.tml.mosaic.cube.MCube;

import static io.github.tml.systemLog.config.Constant.PLUGIN_ID;


@MCube(value=PLUGIN_ID,
        name="系统日志",
        description = "用于输出系统日志的插件",
        version = "1.0.1")
public class SystemLogCube extends Cube {

    public SystemLogCube(GUID id) {
        super(id);
    }

    public void log(String message){
        System.out.println(message);
    }

    public void error(String message){
        System.err.println(message);
    }
}
