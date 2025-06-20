package io.github.tml.mosaic.core.chunk;

import io.github.tml.mosaic.util.ChunkHotSwapUtil;
import io.github.tml.mosaic.util.CubeTemplateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author welsir
 * @description :
 * @date 2025/6/16
 */
public class ChunkManager {

    private static final Map<String,String> classProxyCode = new HashMap<>();

    public enum InsertType {
        REPLACE_ASSIGN_RIGHT,   //等号右边复制修改
        INSERT_AFTER        //行号下插入代码段
    }


    /**
     *
     * @param fullName 全限定类名
     * @param targetLine 目标行数
     * @param type 插入类型
     * @param proxyCode 插入的代码
     * @return 增强后的源代码字符串
     */
    public static String proxyCodeByFullName(String fullName,int targetLine,InsertType type,String proxyCode) {

        String code = getProxyCode(fullName);
        String proxy = ChunkHotSwapUtil.modify(code,
                targetLine,
                type,
                () -> proxyCode,
                Set.of(CubeTemplateUtil.getCubeImportPath()));
        classProxyCode.put(fullName, proxy);
        return proxy;
    }

    public static String getProxyCode(String fullName) {
        return classProxyCode.get(fullName)==null ? ChunkHotSwapUtil.decompileClassFromClassName(fullName) : classProxyCode.get(fullName);
    }
}