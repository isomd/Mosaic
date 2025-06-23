package io.github.tml.mosaic.core.doc.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 描述: 抽象文档乐高基座
 *
 * @author suifeng
 * 日期: 2025/6/23
 */
public abstract class AbstractTmlDoc implements TmlDoc {

    private final Map<String, Object> properties;

    protected AbstractTmlDoc(Map<String, Object> properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public void put(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public Object get(String key) {
        return properties.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Stream<T> children(String key, Function<Map<String, Object>, T> constructor) {
        return Stream.ofNullable(get(key))
                .filter(Objects::nonNull)
                .map(el -> (List<Map<String, Object>>) el)
                .findAny()
                .stream()
                .flatMap(Collection::stream)
                .map(constructor);
    }

    @Override
    public String toString() {
        return properties.toString();
    }
}
