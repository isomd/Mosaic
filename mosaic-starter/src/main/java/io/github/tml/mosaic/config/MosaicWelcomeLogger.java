package io.github.tml.mosaic.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MosaicWelcomeLogger implements ApplicationRunner {

    private static final String githubUrl = "https://github.com/Time-Machine-Lab/Mosaic";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("🧱 [{}] Already Start,Click here http://localhost:9000/mosaic/view/index.html to Enjoy it .You can goto the {} to support us,THX 😘! ", "Mosaic", githubUrl);
    }

}
