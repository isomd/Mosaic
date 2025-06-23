package io.github.tml.mosaic.core.doc.features.extend;

import io.github.tml.mosaic.core.doc.core.TmlDoc;
import io.github.tml.mosaic.core.doc.part.Validation;
import java.util.Map;
import java.util.Optional;

/**
 * 描述: 扩展特征（验证器）
 *
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmValidation extends TmlDoc {

    String VALIDATION_PROPERTY = "validation";

    @SuppressWarnings("unchecked")
    default Optional<Validation> getValidation() {
        return Optional.ofNullable((Map<String, Object>) get(VALIDATION_PROPERTY)).map(Validation::new);
    }
}
