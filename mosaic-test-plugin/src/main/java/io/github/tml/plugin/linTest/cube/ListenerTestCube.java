package io.github.tml.plugin.linTest.cube;

import io.github.tml.mosaic.cube.CubeConfig;
import io.github.tml.mosaic.cube.external.MCube;
import io.github.tml.mosaic.cube.external.MosaicCube;

import static io.github.tml.plugin.linTest.config.Constant.PLUGIN_ID;

@MCube(
        name = "功能型方块",
        description = "测试功能型方块的监听器",
        version = "1.0.0",
        model = "function"
)
public class ListenerTestCube extends MosaicCube {

    @Override
    public boolean init() {
        try {
            // 获取配置信息
            CubeConfig config = getCubeConfig();
            System.out.println("[功能型方块] 功能型方块插件初始化成功");
            System.out.println("[功能型方块] 配置信息: " + config.getAllConfigs());
            return true;
        } catch (Exception e) {
            System.err.println("[功能型方块] 初始化失败: " + e.getMessage());
            return false;
        }
    }

    public void test() {
        System.out.println("test一手");
    }

    @Override
    public String cubeId() {
        return PLUGIN_ID;
    }

    @Override
    public boolean destroy() {
        try {
            System.out.println("[功能型方块] 功能型方块插件已销毁");
            return true;
        } catch (Exception e) {
            System.err.println("[功能型方块] 销毁插件时发生异常: " + e.getMessage());
            return false;
        }
    }
}