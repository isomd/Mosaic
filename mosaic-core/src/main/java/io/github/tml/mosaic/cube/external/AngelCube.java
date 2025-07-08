package io.github.tml.mosaic.cube.external;

/**
 * 描述: 天使方块 - 具有守护标识的抽象方块基类
 *  * 提供 start/stop 方法定义
 * @author suifeng
 * 日期: 2025/7/8
 */
public abstract class AngelCube extends MosaicCube {
    
    /**
     * 启动守护功能
     * 子类实现具体的启动逻辑
     */
    public abstract void start();
    
    /**
     * 停止守护功能
     * 子类实现具体的停止逻辑
     */
    public abstract void stop();
}