package io.github.tml.mosaic.util;

import java.util.Arrays;

/**
 * @author welsir
 * @description :
 * @date 2025/6/8
 */
public class TestUtil {

    public static String setSlotName(String slotName) {
        return slotName;
    }

    public static String execute(String[] params) {
        System.out.println("时代少年团我们喜欢你，我们喜欢"+ Arrays.toString(params) +".");
        return Arrays.toString(params);
    }

}