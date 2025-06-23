package io.github.tml.mosaic.doc.part;

import io.github.tml.mosaic.doc.core.AbstractTmlDoc;
import io.github.tml.mosaic.doc.features.common.TmDesc;
import io.github.tml.mosaic.doc.features.common.TmName;
import io.github.tml.mosaic.doc.features.common.TmType;

import java.util.Map;

public class MetaData extends AbstractTmlDoc implements TmName, TmType, TmDesc {
    public MetaData(Map<String, Object> properties) { super(properties); }
}
