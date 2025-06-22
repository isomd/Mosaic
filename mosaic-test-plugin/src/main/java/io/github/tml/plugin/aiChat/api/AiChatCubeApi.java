package io.github.tml.plugin.aiChat.api;

import io.github.tml.mosaic.cube.external.*;
import io.github.tml.plugin.aiChat.cube.AiChatCube;
import com.alibaba.fastjson.JSONObject;

import static io.github.tml.plugin.aiChat.config.Constant.PLUGIN_ID;

@MExtensionPackage(
        name = "AI问答扩展包",
        description = "提供AI问答功能的扩展包",
        cubeId = PLUGIN_ID
)
public class AiChatCubeApi extends MosaicExtPackage<AiChatCube> {
    
    @MResultItem(
            name = {"answer", "timestamp", "model", "success"},
            description = {"AI回答内容", "响应时间戳", "使用的模型", "是否成功"},
            type = {String.class, Long.class, String.class, Boolean.class}
    )
    @MExtension(
            extPointId = "ai.chat.simple",
            name = "简单AI问答",
            description = "提供基础的AI问答功能",
            priority = 1
    )
    public MosaicResult simpleChat(String question) {
        long startTime = System.currentTimeMillis();
        
        try {
            String answer = mosaicCube.chat(question);
            
            return MosaicResult.build()
                    .put("answer", answer)
                    .put("timestamp", System.currentTimeMillis())
                    .put("model", mosaicCube.getCubeConfig().getConfig("model", String.class))
                    .put("success", true)
                    .put("responseTime", System.currentTimeMillis() - startTime)
                    .build();
                    
        } catch (Exception e) {
            return MosaicResult.build()
                    .put("answer", "AI调用失败: " + e.getMessage())
                    .put("timestamp", System.currentTimeMillis())
                    .put("model", mosaicCube.getCubeConfig().getConfig("model", String.class))
                    .put("success", false)
                    .put("responseTime", System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    @MResultItem(
            name = {"answer", "timestamp", "model", "success", "systemPrompt"},
            description = {"AI回答内容", "响应时间戳", "使用的模型", "是否成功", "系统提示词"},
            type = {String.class, Long.class, String.class, Boolean.class, String.class}
    )
    @MExtension(
            extPointId = "ai.chat.advanced",
            name = "高级AI问答",
            description = "支持自定义系统提示词的AI问答功能",
            priority = 2
    )
    public MosaicResult advancedChat(String question, String systemPrompt) {
        long startTime = System.currentTimeMillis();
        
        try {
            String answer = mosaicCube.chat(question, systemPrompt);
            
            return MosaicResult.build()
                    .put("answer", answer)
                    .put("timestamp", System.currentTimeMillis())
                    .put("model", mosaicCube.getCubeConfig().getConfig("model", String.class))
                    .put("success", true)
                    .put("systemPrompt", systemPrompt)
                    .put("responseTime", System.currentTimeMillis() - startTime)
                    .build();
                    
        } catch (Exception e) {
            return MosaicResult.build()
                    .put("answer", "AI调用失败: " + e.getMessage())
                    .put("timestamp", System.currentTimeMillis())
                    .put("model", mosaicCube.getCubeConfig().getConfig("model", String.class))
                    .put("success", false)
                    .put("systemPrompt", systemPrompt)
                    .put("responseTime", System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    @MResultItem(
            name = {"config", "status"},
            description = {"当前配置信息", "插件状态"},
            type = {JSONObject.class, String.class}
    )
    @MExtension(
            extPointId = "ai.chat.status",
            name = "获取AI插件状态",
            description = "获取AI插件的当前配置和运行状态",
            priority = 10
    )
    public MosaicResult getStatus() {
        return MosaicResult.build()
                .put("config", mosaicCube.getCubeConfig())
                .put("status", "running")
                .put("timestamp", System.currentTimeMillis())
                .build();
    }
    
    @Override
    public String extPackageId() {
        return "ai.chat.api";
    }
}