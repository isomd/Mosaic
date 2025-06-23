package io.github.tml.mosaic.test;

import io.github.tml.mosaic.test.pojo.Order;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ECommerceDemo {

    public static void main(String[] args) {
        System.out.println("--- 模拟接收和构建订单数据 ---");

        // 1. 从内到外构建数据，模拟JSON结构
        // 商品1
        Map<String, Object> product1Map = new HashMap<>();
        product1Map.put("name", "高端机械键盘");
        product1Map.put("price", 899);
        product1Map.put("features", Arrays.asList("RGB", "Hot-swappable")); // 动态属性

        // 商品2
        Map<String, Object> product2Map = new HashMap<>();
        product2Map.put("name", "人体工学鼠标");
        product2Map.put("price", 450);
        product2Map.put("features", Arrays.asList("RGB", "RTGX-swappable")); // 动态属性

        // 行项目1
        Map<String, Object> lineItem1Map = new HashMap<>();
        lineItem1Map.put("quantity", 1);
        lineItem1Map.put("product", product1Map); // 嵌套商品

        // 行项目2
        Map<String, Object> lineItem2Map = new HashMap<>();
        lineItem2Map.put("quantity", 2);
        lineItem2Map.put("product", product2Map);

        // 客户
        Map<String, Object> customerMap = new HashMap<>();
        customerMap.put("name", "张三");
        customerMap.put("email", "zhangsan@example.com"); // 客户可以有动态属性

        // 最终的订单
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", "ORD-2025-12345");
        orderMap.put("status", "PAID");
        orderMap.put("promoCode", "WINTERSALE"); // 订单的动态属性
        orderMap.put("customer", customerMap); // 嵌套客户
        orderMap.put("lineItems", Arrays.asList(lineItem1Map, lineItem2Map)); // 嵌套行项目列表

        // 2. 将原始的 Map 数据包装成我们的 Order 对象
        Order order = new Order(orderMap);

        System.out.println("\n--- 使用模型优雅地读取订单信息 ---");

        // 读取顶层信息
        System.out.println("订单ID: " + order.getId().orElse("N/A"));
        System.out.println("订单状态 (动态属性): " + order.get("status"));
        System.out.println("促销码 (动态属性): " + order.get("promoCode"));

        // 读取【嵌套对象】: Customer
        order.getCustomer().ifPresent(customer -> {
            System.out.println("客户姓名: " + customer.getName().orElse("匿名"));
        });

        // 遍历【嵌套对象列表】: LineItems
        System.out.println("\n--- 订单商品详情 ---");
        order.getLineItems().forEach(item -> {
            // 从行项目中获取数量
            int quantity = item.getQuantity().map(Number::intValue).orElse(0);

            // 从行项目中获取【更深层嵌套】的 Product 对象并读取其信息
            item.getProduct().ifPresent(product -> {
                String productName = product.getName().orElse("未知商品");
                int price = product.getPrice().map(Number::intValue).orElse(0);
                System.out.printf("  - 商品: %s, 单价: %d, 数量: %d, 小计: %d\n",
                        productName, price, quantity, price * quantity);
                
                // 读取商品上的动态属性
                Object features = product.get("features");
                if (features != null) {
                    System.out.println("    特性: " + features);
                }
            });
        });
    }
}