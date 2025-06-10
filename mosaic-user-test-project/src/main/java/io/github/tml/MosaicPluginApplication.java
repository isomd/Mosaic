
package io.github.tml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Genius
 * @date 2023/04/20 00:16
 **/

@SpringBootApplication
@EnableScheduling
public class MosaicPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(MosaicPluginApplication.class, args);
    }
}
