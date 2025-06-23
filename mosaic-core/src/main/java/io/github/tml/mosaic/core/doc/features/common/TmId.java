package io.github.tml.mosaic.core.doc.features.common;

import io.github.tml.mosaic.core.doc.core.TmlDoc;

import java.util.Optional;

/**
 * 描述: 通用特征（ID）
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmId extends TmlDoc {

    String ID_PROPERTY = "id";

    default Optional<String> getId() {
        return Optional.ofNullable((String) get(ID_PROPERTY));
    }
}
