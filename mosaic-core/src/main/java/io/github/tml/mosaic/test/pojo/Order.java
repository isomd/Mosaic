package io.github.tml.mosaic.test.pojo;

import io.github.tml.mosaic.test.AbstractDocument;
import io.github.tml.mosaic.test.component.HasCustomer;
import io.github.tml.mosaic.test.component.HasId;
import io.github.tml.mosaic.test.component.HasLineItems;

import java.util.Map;

public class Order extends AbstractDocument implements HasId, HasCustomer, HasLineItems {
    public Order(Map<String, Object> properties) { super(properties); }
}