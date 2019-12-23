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
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                getPropertiesKeySerializer());
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                getPropertiesValueSerializer());

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
