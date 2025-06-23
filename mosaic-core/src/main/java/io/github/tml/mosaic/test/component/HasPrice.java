package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;

import java.util.Optional;

public interface HasPrice extends Document {
    String PRICE_PROPERTY = "price";
    default Optional<Number> getPrice() {
        return Optional.ofNullable((Number) get(PRICE_PROPERTY));
    }
}