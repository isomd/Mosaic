package io.github.tml.mosaic.test.pojo;

import io.github.tml.mosaic.test.AbstractDocument;
import io.github.tml.mosaic.test.component.HasName;

import java.util.Map;

public class Customer extends AbstractDocument implements HasName {
    public Customer(Map<String, Object> properties) { super(properties); }
}