package io.github.tml.mosaic.core.doc.part;

import io.github.tml.mosaic.core.doc.core.AbstractTmlDoc;
import io.github.tml.mosaic.core.doc.features.common.TmDesc;
import io.github.tml.mosaic.core.doc.features.common.TmName;
import io.github.tml.mosaic.core.doc.features.common.TmType;

import java.util.Map;

public class MetaData extends AbstractTmlDoc implements TmName, TmType, TmDesc {
    public MetaData(Map<String, Object> properties) { super(properties); }
}
