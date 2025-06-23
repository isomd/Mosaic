package io.github.tml.mosaic.test.pojo;

import io.github.tml.mosaic.test.AbstractDocument;
import io.github.tml.mosaic.test.component.HasName;
import io.github.tml.mosaic.test.component.HasPrice;

import java.util.Map;

public class Product extends AbstractDocument implements HasName, HasPrice {
    public Product(Map<String, Object> properties) { super(properties); }
}