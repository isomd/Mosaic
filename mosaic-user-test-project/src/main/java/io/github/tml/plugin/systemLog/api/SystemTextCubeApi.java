package io.github.tml.plugin.systemLog.api;

import io.github.tml.mosaic.cube.external.*;
import io.github.tml.plugin.systemLog.cube.SystemLogCube;

import static io.github.tml.plugin.systemLog.config.Constant.PLUGIN_ID;

@MExtensionPackage(
        name = "系统文本返回拓展包",
        description = "用于系统日志输出",
        cubeId = PLUGIN_ID)
public class SystemTextCubeApi extends MosaicExtPackage<SystemLogCube> {

    @MResultItem(name={"error", "system" , "threadId"},
            description = {"错误信息", "系统信息", "线程id"},
            type ={String.class, String.class, Long.class})
    @MExtension(extPointId = "text.get", name = "获取系统文本", description = "获取系统文本", priority = 1)
    public MosaicResult getSystemText(String msg, boolean needError){
        MosaicResult.Builder resBuilder = MosaicResult.build();
        if(needError){
            String res = "[ERROR] " + msg;
            resBuilder.put("error", res);
            mosaicCube.error(res);
        }
        String res = "[System] " + msg;
        resBuilder.put("system", res);
        mosaicCube.log(res);

        resBuilder.put("threadId", Thread.currentThread().getId());
        return resBuilder.build();
    }

    @Override
    public String extPackageId() {
        return "system.text";
    }
}
