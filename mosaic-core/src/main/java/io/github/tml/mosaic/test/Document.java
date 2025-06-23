package io.github.tml.mosaic.test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Document {
    void put(String key, Object value);
    Object get(String key);
    <T> Stream<T> children(String key, Function<Map<String, Object>, T> constructor);
}