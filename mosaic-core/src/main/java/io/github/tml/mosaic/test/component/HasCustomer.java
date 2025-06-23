package io.github.tml.mosaic.test.component;

import io.github.tml.mosaic.test.Document;
import io.github.tml.mosaic.test.pojo.Customer;

import java.util.Map;
import java.util.Optional;

public interface HasCustomer extends Document {
    String CUSTOMER_PROPERTY = "customer";
    @SuppressWarnings("unchecked")
    default Optional<Customer> getCustomer() {
        // 这是处理【对象中的对象】的关键！
        // 我们取出嵌套的 Map，然后用它来构造一个新的 Customer 对象。
        return Optional.ofNullable((Map<String, Object>) get(CUSTOMER_PROPERTY))
                       .map(Customer::new);
    }
}