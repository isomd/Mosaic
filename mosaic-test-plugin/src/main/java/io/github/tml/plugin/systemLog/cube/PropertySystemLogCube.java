package io.github.tml.plugin.systemLog.cube;

import cn.hutool.core.date.DateTime;
import io.github.tml.mosaic.cube.CubeConfig;
import io.github.tml.mosaic.cube.external.MCube;
import io.github.tml.mosaic.cube.external.MosaicCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID_P;

@MCube(name = "系统日志（多例测试）",
        description = "用于输出系统日志的插件",
        version = "1.0.1", model = "property")
public class PropertySystemLogCube extends MosaicCube {

    String outputFormat = "";

    Integer maxFileSize;

    public PropertySystemLogCube() {
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void error(String message) {
        System.err.println(message);
    }

    @Override
    public boolean init() {

        DateTime aaa = new DateTime();

        CubeConfig cubeConfig = getCubeConfig();
        outputFormat = cubeConfig.getConfig("outputFormat", String.class);
        maxFileSize = cubeConfig.getConfig("maxFileSize", Integer.class);

        System.out.println(outputFormat + "-------------------------------------");

        System.out.println(maxFileSize + "-------------------------------------");
        return true;
    }

    @Override
    public String cubeId() {
        return PLUGIN_ID_P;
    }
}
