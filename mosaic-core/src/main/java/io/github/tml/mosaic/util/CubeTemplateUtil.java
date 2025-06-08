package io.github.tml.mosaic.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
public class CubeTemplateUtil {

    public static String generateCubeTemplateBySlotName(String slotName){
        return "TestUtil.setSlotName(\""+slotName+"\")";
    }

    public static String generateCubeTemplateByParams(List<String> params){
        String joined = params.stream()
                .map(p -> "\"" + p + "\"") // 确保加引号
                .collect(Collectors.joining(", "));
        return "TestUtil.execute(new String[]{" + joined + "})";
    }

    public static String getCubeImportPath(){
        return "com.agentscript.util.TestUtil";
    }

}