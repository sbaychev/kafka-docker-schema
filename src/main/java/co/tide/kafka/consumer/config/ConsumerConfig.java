package co.tide.kafka.consumer.config;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Getter
@Setter
@Configuration
@Profile("!test")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class ConsumerConfig {

    private String propertiesKeyDeserializer;
    private String propertiesValueDeserializer;
    private String autoOffsetReset;
    private String groupId;
    private boolean propertiesSpecificAvroReader;
    private boolean propertiesEnableAutoCommit;
    private String propertiesAutoCommitIntervalMs;
    private int propertiesPollTimeout;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapKafkaServers;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

//      spring.kafka.input.content-type=application/*+avro
//      spring.kafka.input.concurrency=3

    @Bean
    public Map<String, Object> avroConfigsConsumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                getBootStrapKafkaServers());
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                getSchemaRegistryUrl());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                getPropertiesKeyDeserializer());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                getPropertiesValueDeserializer());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG,
                getGroupId());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                getAutoOffsetReset());
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
                isPropertiesSpecificAvroReader());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                isPropertiesEnableAutoCommit());
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                getPropertiesAutoCommitIntervalMs());

        return props;
    }

    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {

        return new DefaultKafkaConsumerFactory<>(avroConfigsConsumerFactory());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Object, Object>> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setPollTimeout(getPropertiesPollTimeout());

        /*  In case of DLQ needed Implementation */

//        factory.setErrorHandler(new SeekToCurrentErrorHandler(
//                new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(0L, 2))); // dead-letter (topic DLQ needs to be created) after 3 tries)

        return factory;
    }

    /*  Programmatic Way for topic Creation */

//    @Bean
//    NewTopic newTopic() {
//        return new NewTopic(topicName, partitions, replicationFactor);
//    }

}
