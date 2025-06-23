package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;
import io.github.tml.mosaic.test.pojo.LineItem;

import java.util.stream.Stream;

public interface HasLineItems extends Document {
    String ITEMS_PROPERTY = "lineItems";
    default Stream<LineItem> getLineItems() {
        // 这是处理【对象中的对象列表】的标准方式
        return children(ITEMS_PROPERTY, LineItem::new);
    }
}