package io.github.tml.mosaic.doc.features.extend;

import io.github.tml.mosaic.doc.core.TmlDoc;

import java.util.Optional;

/**
 * 描述: 扩展特征（是否必要）
 *
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmRequired extends TmlDoc {

    String REQUIRED_PROPERTY = "required";

    default Optional<Boolean> getRequired() {
        return Optional.ofNullable((Boolean) get(REQUIRED_PROPERTY));
    }
}
