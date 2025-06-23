package io.github.tml.mosaic.core.doc.part;

import io.github.tml.mosaic.core.doc.core.AbstractTmlDoc;
import io.github.tml.mosaic.core.doc.features.extend.TmMetaData;
import io.github.tml.mosaic.core.doc.features.extend.TmValue;

import java.util.Map;

public class Config extends AbstractTmlDoc implements TmMetaData, TmValue {
    public Config(Map<String, Object> properties) { super(properties); }
}
