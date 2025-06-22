package io.github.tml.mosaic.hotSwap;

import io.github.tml.mosaic.hotSwap.model.DeployVersion;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */
@Getter
public class HotSwapContext {

    private final Map<String,String> classProxyCode = new HashMap<>();

    private final Map<String, DeployVersion> hotSwapRecord = new HashMap<>();

    public enum InsertType {
        REPLACE_ASSIGN_RIGHT,   //等号右边复制修改
        INSERT_AFTER        //行号下插入代码段
    }

    public void putClassProxyCode(String className, String proxyCode) {
        classProxyCode.put(className, proxyCode);
    }

    public Boolean ProxyCodeContainsKey(String className) {
        return classProxyCode.containsKey(className);
    }

    public String getProxyCode(String className) {
        return classProxyCode.get(className);
    }
}