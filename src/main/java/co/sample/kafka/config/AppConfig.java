package co.sample.kafka.config;

import co.sample.kafka.producer.config.ProducerConfig;
import co.sample.kafka.consumer.config.ConsumerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * General configuration of the application.
 *
 */
@ComponentScan(basePackages = "co.sample.kafka")
@Import({ConsumerConfig.class, ProducerConfig.class})
public class AppConfig {

}
