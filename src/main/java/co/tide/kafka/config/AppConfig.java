package co.tide.kafka.config;

import co.tide.kafka.consumer.config.ConsumerConfig;
import co.tide.kafka.producer.config.ProducerConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * General configuration of the application.
 *
 * @author Tide
 */
@ComponentScan(basePackages = "co.tide.kafka")
@Import({ConsumerConfig.class, ProducerConfig.class})
public class AppConfig {

}
