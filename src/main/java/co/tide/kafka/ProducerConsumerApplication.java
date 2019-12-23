package co.tide.kafka;

import co.tide.kafka.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(AppConfig.class)
@SpringBootApplication
public class ProducerConsumerApplication {

    public static void main(final String... args) {
        SpringApplication.run(ProducerConsumerApplication.class, args);
    }
}
