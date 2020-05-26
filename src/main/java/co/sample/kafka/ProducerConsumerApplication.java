package co.sample.kafka;

import co.sample.kafka.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@Import(AppConfig.class)
@EnableKafka
@SpringBootApplication
public class ProducerConsumerApplication {

    public static void main(final String... args) {
        SpringApplication.run(ProducerConsumerApplication.class, args);
    }
}
