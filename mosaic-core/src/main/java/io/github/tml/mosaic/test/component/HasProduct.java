package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;
import io.github.tml.mosaic.test.pojo.Product;

import java.util.Map;
import java.util.Optional;

public interface HasProduct extends Document {
    String PRODUCT_PROPERTY = "product";
    @SuppressWarnings("unchecked")
    default Optional<Product> getProduct() {
        // 与 HasCustomer 类似，处理嵌套的 Product 对象
        return Optional.ofNullable((Map<String, Object>) get(PRODUCT_PROPERTY))
                       .map(Product::new);
    }
}