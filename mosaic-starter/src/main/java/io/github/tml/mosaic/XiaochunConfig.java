package io.github.tml.mosaic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XiaochunConfig {
    Integer i = 0;
    @Bean
    public XiaochunBean xiaochunBean() {
        i ++;
        return new XiaochunBean(i);
    }
}
