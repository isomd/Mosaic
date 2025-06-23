package io.github.tml.mosaic.doc.features.extend;

import io.github.tml.mosaic.doc.core.TmlDoc;

import java.util.Optional;

public interface TmDefaultValue extends TmlDoc {

    String DEFAULT_VALUE_PROPERTY = "defaultValue";

    default Optional<Object> getDefaultValue() {
        return Optional.ofNullable((Object) get(DEFAULT_VALUE_PROPERTY));
    }
}
