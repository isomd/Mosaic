package io.github.tml.plugin.linTest.cube;

import io.github.tml.mosaic.cube.CubeConfig;
import io.github.tml.mosaic.cube.external.MCube;
import io.github.tml.mosaic.cube.external.MosaicCube;
import static io.github.tml.plugin.linTest.config.Constant.PLUGIN_ID;

@MCube(
        name = "功能性方块测试",
        description = "测试功能性方块的Listener组件",
        version = "1.0.0",
        scope = "singleton",
        model = "function"
)
public class ListenerTestCube extends MosaicCube {

    @Override
    public boolean init() {
        try {
            // 获取配置信息
            CubeConfig config = getCubeConfig();
            System.out.println(config.toString());
            return true;
        } catch (Exception e) {
            System.err.println("[Listener-Test] 初始化失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String cubeId() {
        return PLUGIN_ID;
    }

    @Override
    public boolean destroy() {
        try {
            System.out.println("[Listener-Test] 功能性方块测试插件已销毁");
            return true;
        } catch (Exception e) {
            System.err.println("[Listener-Test] 销毁插件时发生异常: " + e.getMessage());
            return false;
        }
    }
}