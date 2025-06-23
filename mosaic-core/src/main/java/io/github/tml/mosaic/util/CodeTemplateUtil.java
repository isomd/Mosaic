package io.github.tml.mosaic.util;

import java.util.List;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
public class CodeTemplateUtil {

    //  GoldenShovel.executeBootStrap().slotId("xxxxx").run("xxxxx");

    public static String buildCodeTemplate(String slotId, List<String> params){
        StringBuilder sb = new StringBuilder("GoldenShovel");
        sb.append(".executeBootStrap()")
                .append(".slotId(\""+slotId+"\")")
                .append(".run("+buildParamsStr(params)+");");

        return sb.toString();
    }

    private static String buildParamsStr(List<String> params) {
        // 确保加引号
        return String.join(", ", params);
    }

    public static String getCubeImportPath(){
        return "io.github.tml.mosaic.GoldenShovel";
    }

}