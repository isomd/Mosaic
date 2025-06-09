package io.github.tml.mosaic.install.enhance;

import io.github.tml.mosaic.install.InstallationConfig;
import io.github.tml.mosaic.install.InstallationItem;
import io.github.tml.mosaic.install.support.ResourceFileType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述: InstallationConfig的后置增强处理抽象类
 * @author suifeng
 * 日期: 2025/06/09
 */
public abstract class AbstractInstallationConfigEnhancer implements InstallationConfigEnhancer {

    @Override
    public final void enhance(InstallationConfig installationConfig) {
        if (installationConfig == null) {
            return;
        }

        // 确保installations不为null
        if (installationConfig.getInstallations() == null) {
            installationConfig.setInstallations(new ArrayList<>());
        }

        // 执行前置处理
        beforeEnhance(installationConfig);

        // 执行核心增强逻辑
        doEnhance(installationConfig);

        // 执行后置处理
        afterEnhance(installationConfig);
    }

    /**
     * 核心增强逻辑，子类可以重写此方法实现完全自定义的逻辑
     */
    protected void doEnhance(InstallationConfig installationConfig) {
        List<InstallationItem> existingItems = installationConfig.getInstallations();

        // 获取需要添加的安装项
        List<InstallationItem> newItems = collectInstallationItems(existingItems);

        // 过滤和验证
        List<InstallationItem> validItems = filterAndValidateItems(newItems, existingItems);

        // 添加到配置中
        addItemsToConfig(installationConfig, validItems);
    }

    /**
     * 收集需要添加的InstallationItem，提供多种扩展方式
     */
    protected List<InstallationItem> collectInstallationItems(List<InstallationItem> existingItems) {
        return Stream.of(
                        // 方式1: 批量添加多个项目
                        addInstallationItems(existingItems),
                        // 方式2: 添加单个项目（转换为列表）
                        wrapSingleItem(addInstallationItem()),
                        // 方式3: 基于现有配置动态生成
                        generateDynamicItems(existingItems)
                )
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 过滤和验证InstallationItem
     * 子类可以重写此方法实现自定义的过滤逻辑
     */
    protected List<InstallationItem> filterAndValidateItems(List<InstallationItem> newItems,
                                                            List<InstallationItem> existingItems) {
        return newItems.stream()
                .filter(this::isValidItem)
                .filter(item -> !isDuplicate(item, existingItems))
                .collect(Collectors.toList());
    }

    /**
     * 添加多个InstallationItem的扩展点
     * 子类重写此方法可以批量添加多个安装项
     */
    protected List<InstallationItem> addInstallationItems(List<InstallationItem> existingItems) {
        return Collections.emptyList();
    }

    /**
     * 添加单个InstallationItem的扩展点
     * 子类重写此方法可以添加单个安装项，实现最简单
     */
    protected InstallationItem addInstallationItem() {
        return null;
    }

    /**
     * 基于现有配置动态生成InstallationItem的扩展点
     * 子类重写此方法可以基于现有配置进行条件性添加
     */
    protected List<InstallationItem> generateDynamicItems(List<InstallationItem> existingItems) {
        return Collections.emptyList();
    }

    /**
     * 验证InstallationItem是否有效
     */
    protected boolean isValidItem(InstallationItem item) {
        return item != null && item.getType() != null &&
                item.getLocation() != null && !item.getLocation().trim().isEmpty();
    }

    /**
     * 检查是否为重复项
     */
    protected boolean isDuplicate(InstallationItem newItem, List<InstallationItem> existingItems) {
        return existingItems.stream()
                .anyMatch(existing -> isSameItem(newItem, existing));
    }

    /**
     * 判断两个InstallationItem是否相同
     */
    protected boolean isSameItem(InstallationItem item1, InstallationItem item2) {
        return Objects.equals(item1.getType(), item2.getType()) &&
                Objects.equals(item1.getLocation(), item2.getLocation());
    }

    /**
     * 将验证后的项目添加到配置中
     */
    protected void addItemsToConfig(InstallationConfig config, List<InstallationItem> items) {
        if (!items.isEmpty()) {
            config.getInstallations().addAll(items);
        }
    }

    /**
     * 前置处理钩子方法
     */
    protected void beforeEnhance(InstallationConfig installationConfig) {
        // 默认空实现，子类可选择性重写
    }

    /**
     * 后置处理钩子方法
     */
    protected void afterEnhance(InstallationConfig installationConfig) {
        // 默认空实现，子类可选择性重写
    }

    /**
     * 工具方法：将单个项目包装为列表
     */
    private List<InstallationItem> wrapSingleItem(InstallationItem item) {
        return item != null ? Collections.singletonList(item) : Collections.emptyList();
    }

    /**
     * 工具方法：创建InstallationItem的便捷方法
     */
    protected InstallationItem createInstallationItem(ResourceFileType type, String location) {
        InstallationItem item = new InstallationItem();
        item.setType(type);
        item.setLocation(location);
        return item;
    }

    /**
     * 工具方法：创建带属性的InstallationItem
     */
    protected InstallationItem createInstallationItem(ResourceFileType type, String location,
                                                      Map<String, Object> properties) {
        InstallationItem item = createInstallationItem(type, location);
        item.setProperties(properties);
        return item;
    }

    /**
     * 工具方法：安全地合并多个InstallationItem列表
     */
    protected List<InstallationItem> mergeInstallationItems(List<InstallationItem>... itemLists) {
        return Arrays.stream(itemLists)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 工具方法：根据类型过滤InstallationItem
     */
    protected List<InstallationItem> filterByType(List<InstallationItem> items, ResourceFileType type) {
        return items.stream()
                .filter(item -> Objects.equals(item.getType(), type))
                .collect(Collectors.toList());
    }

    /**
     * 工具方法：检查是否存在指定类型的InstallationItem
     */
    protected boolean hasItemOfType(List<InstallationItem> items, ResourceFileType type) {
        return items.stream()
                .anyMatch(item -> Objects.equals(item.getType(), type));
    }
}