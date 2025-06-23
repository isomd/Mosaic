package io.github.tml.mosaic.doc.features.extend;

import io.github.tml.mosaic.doc.core.TmlDoc;
import io.github.tml.mosaic.doc.part.Value;

import java.util.Map;
import java.util.Optional;

/**
 * 描述: 扩展特征（value）
 *
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmValue extends TmlDoc {

    String VALUE_PROPERTY = "value";

    @SuppressWarnings("unchecked")
    default Optional<Value> getValue() {
        return Optional.ofNullable((Map<String, Object>) get(VALUE_PROPERTY)).map(Value::new);
    }
}
