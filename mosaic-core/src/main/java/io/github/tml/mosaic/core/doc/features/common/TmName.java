package io.github.tml.mosaic.core.doc.features.common;

import io.github.tml.mosaic.core.doc.core.TmlDoc;

import java.util.Optional;

/**
 * 描述: 通用特征（Name）
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmName extends TmlDoc {

    String NAME_PROPERTY = "name";

    default Optional<String> getName() {
        return Optional.ofNullable((String) get(NAME_PROPERTY));
    }
}
