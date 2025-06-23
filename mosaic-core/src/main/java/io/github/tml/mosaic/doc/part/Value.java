package io.github.tml.mosaic.doc.part;

import io.github.tml.mosaic.doc.core.AbstractTmlDoc;
import io.github.tml.mosaic.doc.features.extend.TmDefaultValue;
import io.github.tml.mosaic.doc.features.extend.TmRequired;
import io.github.tml.mosaic.doc.features.extend.TmValidation;

import java.util.Map;

public class Value extends AbstractTmlDoc implements TmRequired, TmDefaultValue, TmValidation {
    public Value(Map<String, Object> properties) { super(properties); }
}
