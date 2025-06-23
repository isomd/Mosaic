package io.github.tml.mosaic.test.pojo;

import io.github.tml.mosaic.test.AbstractDocument;
import io.github.tml.mosaic.test.component.HasProduct;
import io.github.tml.mosaic.test.component.HasQuantity;

import java.util.Map;

public class LineItem extends AbstractDocument implements HasQuantity, HasProduct {
    public LineItem(Map<String, Object> properties) { super(properties); }
}
