package io.github.tml.mosaic.core.doc.core;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 描述: 抽象文档规则书
 * @author suifeng
 * 日期: 2025/6/23
 */
public interface TmlDoc {

    void put(String key, Object value);

    Object get(String key);

    <T> Stream<T> children(String key, Function<Map<String, Object>, T> constructor);
}
