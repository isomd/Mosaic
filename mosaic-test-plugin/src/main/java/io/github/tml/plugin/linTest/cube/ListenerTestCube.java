package io.github.tml.plugin.linTest.cube;

import io.github.tml.mosaic.cube.CubeConfig;
import io.github.tml.mosaic.cube.external.MCube;
import io.github.tml.mosaic.cube.external.AngelCube;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.tml.plugin.linTest.config.Constant.PLUGIN_ID;

@MCube(
        name = "功能型方块",
        description = "测试功能型方块的监听器",
        version = "1.0.0"
)
public class ListenerTestCube extends AngelCube {

    private final AtomicBoolean flag = new AtomicBoolean(true);
    private ScheduledExecutorService executor;

    @Override
    public boolean init() {
        try {
            CubeConfig config = getCubeConfig();
            System.out.println("[功能型方块] 功能型方块插件初始化成功");
            System.out.println("[功能型方块] 配置信息: " + config.getAllConfigs());
            return true;
        } catch (Exception e) {
            System.err.println("[功能型方块] 初始化失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void start() {
        executor = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "AngelCube-Guardian-" + cubeId());
            thread.setDaemon(true);
            return thread;
        });

        executor.scheduleWithFixedDelay(() -> {
            if (flag.get()) {
                System.out.println("[功能型方块] 守护天使正在巡视 - 状态正常");
            }
        }, 0, 3, TimeUnit.SECONDS);

        System.out.println("[功能型方块] 天使守护已启动");
    }

    @Override
    public void stop() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            System.out.println("[功能型方块] 天使守护已停止");
        }
    }

    public void changeFlag() {
        flag.set(false);
    }

    @Override
    public String cubeId() {
        return PLUGIN_ID;
    }

    @Override
    public boolean destroy() {
        try {
            stop();
            System.out.println("[功能型方块] 功能型方块插件已销毁");
            return true;
        } catch (Exception e) {
            System.err.println("[功能型方块] 销毁插件时发生异常: " + e.getMessage());
            return false;
        }
    }
}