package io.github.tml.mosaic.core.doc.features.common;

import io.github.tml.mosaic.core.doc.core.TmlDoc;

import java.util.Date;
import java.util.Optional;

/**
 * 描述: 通用特征（Time）
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmTime extends TmlDoc {

    String TIME_PROPERTY = "time";

    default Optional<Date> getTime() {
        return Optional.ofNullable((Date) get(TIME_PROPERTY));
    }
}
