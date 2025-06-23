package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;

import java.util.Optional;

public interface HasId extends Document {
    String ID_PROPERTY = "id";
    default Optional<String> getId() {
        return Optional.ofNullable((String) get(ID_PROPERTY));
    }
}