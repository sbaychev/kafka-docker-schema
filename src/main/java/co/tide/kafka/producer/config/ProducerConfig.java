package co.tide.kafka.producer.config;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.kafka.producer")
@EnableAsync
public class ProducerConfig {

    private String propertiesKeySerializer;
    private String propertiesValueSerializer;
    private String propertiesAcks;
    private int propertiesRetries;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapKafkaServers;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Bean
    public Map<String, Object> producerConfigs() {

        Map<String, Object> props = new HashMap<>();

        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                getBootStrapKafkaServers());
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                getSchemaRegistryUrl());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG,
                getPropertiesAcks());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG,
                getPropertiesRetries());
        //Only one in-flight messages per Kafka broker connection
        // - max.in.flight.requests.per.connection (default 5)
        props.put(org.apache.kafka.clients.producer.ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                1);
        //Request timeout - request.timeout.ms
        props.put(org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                10000);
        //Only retry after one second.
        props.put(org.apache.kafka.clients.producer.ProducerConfig.RETRY_BACKOFF_MS_CONFIG,
                1500);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                getPropertiesKeySerializer());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                getPropertiesValueSerializer());

        /*                  To be Considered For Usage           */
//        //Linger up to 100 ms before sending batch if size not met
//        props.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, 100);
//        //Batch up to 64K buffer sizes.
//        props.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG,  16_384 * 4);
//        //Use Snappy compression for batch compression.
//        End to end compression is possible if the Kafka Broker config “compression.type” set to “producer”.
//        props.put(org.apache.kafka.clients.producer.ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return props;
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }

}
