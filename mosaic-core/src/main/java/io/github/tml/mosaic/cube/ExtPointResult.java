package io.github.tml.mosaic.cube;

import io.github.tml.mosaic.cube.factory.definition.PointResultDefinition;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 多参数适配器
 */
public class ExtPointResult {

    public final static String DEFAULT_RETURN_NAME = "default";

    private final Map<String, ExtPointResultItem> items = new HashMap<>();

    public void addResultItem(ExtPointResultItem extPointResultItem){
        Optional.ofNullable(extPointResultItem)
                .map(ExtPointResultItem::getItemName)
                .ifPresent(itemName -> items.put(itemName, extPointResultItem));
    }

    public ExtPointResultItem getResultItem(String itemName){
        return items.get(itemName);
    }

    public boolean containsResultItem(String itemName){
        return items.containsKey(itemName);
    }

    @Data
    public static class ExtPointResultItem{
        private String itemName;
        private Class<?> itemClass;
        private String description;

        public static ExtPointResultItem convertByDefinition(PointResultDefinition.PointResultItemDefinition itemDefinition) {
            ExtPointResult.ExtPointResultItem item = new ExtPointResult.ExtPointResultItem();
            item.setItemName(itemDefinition.getItemName());
            item.setDescription(itemDefinition.getDescription());
            item.setItemClass(itemDefinition.getItemClass());
            return item;
        }
    }
}
