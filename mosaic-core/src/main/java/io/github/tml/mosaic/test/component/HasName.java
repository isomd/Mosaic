package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;

import java.util.Optional;

public interface HasName extends Document {
    String NAME_PROPERTY = "name";
    default Optional<String> getName() {
        return Optional.ofNullable((String) get(NAME_PROPERTY));
    }
}