package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;

import java.util.Optional;

public interface HasQuantity extends Document {
    String QTY_PROPERTY = "quantity";
    default Optional<Number> getQuantity() {
        return Optional.ofNullable((Number) get(QTY_PROPERTY));
    }
}