package io.github.tml.mosaic.core.doc.features.common;

import io.github.tml.mosaic.core.doc.core.TmlDoc;

import java.util.Optional;

/**
 * 描述: 通用特征（Desc)
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmDesc extends TmlDoc {

    String DESC_PROPERTY = "desc";

    default Optional<String> getDesc() {
        return Optional.ofNullable((String) get(DESC_PROPERTY));
    }
}
