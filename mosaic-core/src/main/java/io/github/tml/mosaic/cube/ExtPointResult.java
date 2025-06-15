package io.github.tml.mosaic.cube;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 多参数适配器
 */
public class ExtPointResult {

    private final Map<String, ExtPointResultItem> items = new HashMap<>();

    public void addResultItem(ExtPointResultItem extPointResultItem){
        Optional.ofNullable(extPointResultItem)
                .map(ExtPointResultItem::getItemName)
                .ifPresent(itemName -> items.put(itemName, extPointResultItem));
    }

    public ExtPointResultItem getResultItem(String itemName){
        return items.get(itemName);
    }

    @Data
    public class ExtPointResultItem{
        private String itemName;
        private Class<?> itemClass;
        private String description;
    }
}
