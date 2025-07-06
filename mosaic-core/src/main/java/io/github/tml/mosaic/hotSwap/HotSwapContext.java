package io.github.tml.mosaic.hotSwap;

import io.github.tml.mosaic.hotSwap.model.HotSwapPoint;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author welsir
 * @description :
 * @date 2025/6/22
 */
@Getter
public class HotSwapContext {

    /**
     * 增强后的源码字符串KEY-VALUE
     */
    private final Map<String,String> classProxyCode = new HashMap<>();

    /**
     * 方法级别的类更新点
     */
    private final Map<String, Map<String,List<HotSwapPoint>>> hotSwapPointRecord = new HashMap<>();


    public enum InsertType {

        REPLACE_ASSIGN_RIGHT,   //等号右边复制修改
        INSERT_AFTER;        //行号下插入代码段

        public static InsertType fromString(String value) {
            for (InsertType type : InsertType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid InsertType: " + value);
        }
    }

    //todo: 考虑并发
    public void removeLastHotSwapPoint(String className, String methodName) {
        List<HotSwapPoint> hotSwapPoints = hotSwapPointRecord.get(className).get(methodName);
        hotSwapPoints.remove(hotSwapPoints.size()-1);
    }

    public void putClassProxyCode(String className, String proxyCode) {
        classProxyCode.put(className, proxyCode);
    }

    public Boolean ProxyCodeContainsKey(String className) {
        return classProxyCode.containsKey(className);
    }

    public String getProxyCode(String className) {
        return classProxyCode.get(className);
    }

    public List<HotSwapPoint> getHotSwapPointsByClassName(String className) {
        return Optional.ofNullable(hotSwapPointRecord.get(className))
                .map(Map::values)
                .map(values -> values.stream().flatMap(List::stream).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }


    public void putHotSwapPoint(HotSwapPoint hotSwapPoint) {
        hotSwapPointRecord
                .computeIfAbsent(hotSwapPoint.getClassName(), k -> new HashMap<>())
                .computeIfAbsent(hotSwapPoint.getChangeRecord().getMethodName(), k -> new ArrayList<>())
                .add(hotSwapPoint);
    }

    public Boolean existsHotSwapPoint(String className,String methodName) {
        return Optional.ofNullable(hotSwapPointRecord.get(className))
                .map(methodMap -> methodMap.get(methodName))
                .map(list -> !list.isEmpty())
                .orElse(false);
    }

    public List<HotSwapPoint> getHotSwapPointsByMethodName(String className,String methodName) {
        return Optional.ofNullable(hotSwapPointRecord.get(className))
                .map(methodMap -> methodMap.get(methodName))
                .orElse(Collections.emptyList());
    }


}