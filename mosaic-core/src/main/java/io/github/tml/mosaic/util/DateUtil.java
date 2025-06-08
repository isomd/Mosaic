package io.github.tml.mosaic.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author welsir
 * @description :
 * @date 2025/6/6
 */
public class DateUtil {

    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return currentDate.format(formatter)+"/";
    }

}