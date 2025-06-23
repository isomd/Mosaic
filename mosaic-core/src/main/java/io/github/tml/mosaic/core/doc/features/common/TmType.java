package io.github.tml.mosaic.core.doc.features.common;

import io.github.tml.mosaic.core.doc.core.TmlDoc;

import java.util.Optional;

/**
 * 描述: 通用特征（Type）
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmType extends TmlDoc {

    String TYPE_PROPERTY = "type";

    default Optional<String> getTime() {
        return Optional.ofNullable((String) get(TYPE_PROPERTY));
    }
}
