package io.github.tml.mosaic.doc.features.extend;

import io.github.tml.mosaic.doc.core.TmlDoc;
import io.github.tml.mosaic.doc.part.MetaData;

import java.util.Map;
import java.util.Optional;

public interface TmMetaData extends TmlDoc {

    String METADATA_PROPERTY = "metadata";

    @SuppressWarnings("unchecked")
    default Optional<MetaData> getMetaData() {
        return Optional.ofNullable((Map<String, Object>) get(METADATA_PROPERTY)).map(MetaData::new);
    }
}
