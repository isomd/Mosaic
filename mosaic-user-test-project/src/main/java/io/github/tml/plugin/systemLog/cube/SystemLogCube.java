package io.github.tml.plugin.systemLog.cube;

import io.github.tml.mosaic.cube.MCube;
import io.github.tml.mosaic.cube.external.MosaicCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID;


@MCube(value=PLUGIN_ID,
        name="系统日志",
        description = "用于输出系统日志的插件",
        version = "1.0.1",
        model = "property"
)
public class SystemLogCube extends MosaicCube {

    public void log(String message){
        System.out.println(message);
    }

    public void error(String message){
        System.err.println(message);
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String cubeId() {
        return PLUGIN_ID;
    }
}
