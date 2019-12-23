package co.tide.kafka.config;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

@Configuration
public class MockSerdeConfig {

    // KafkaProperties groups all properties prefixed with `spring.kafka`
    private KafkaProperties kafkaProperties;

    public MockSerdeConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Mock schema registry bean used by Kafka Avro Serde since the @EmbeddedKafka setup doesn't include a schema
     * registry.
     *
     * @return MockSchemaRegistryClient instance
     */
    @Bean
    @Primary
    public SchemaRegistryClient schemaRegistryClient() {
        return new MockSchemaRegistryClient();
    }

    /**
     * KafkaAvroSerializer that uses the MockSchemaRegistryClient
     *
     * @return KafkaAvroSerializer instance
     */
    @Bean
    public KafkaAvroSerializer kafkaAvroSerializer() {
        return new KafkaAvroSerializer(schemaRegistryClient());
    }

    /**
     * KafkaAvroDeserializer that uses the MockSchemaRegistryClient. The props must be provided so that
     * specific.avro.reader: true is set. Without this, the consumer will receive GenericData records.
     *
     * @return KafkaAvroDeserializer instance
     */
    @Bean
    public KafkaAvroDeserializer kafkaAvroDeserializer() {
        return new KafkaAvroDeserializer(schemaRegistryClient(), kafkaProperties.buildConsumerProperties());
    }

    /**
     * Configures the kafka producer factory to use the overridden KafkaAvroDeserializer so that the
     * MockSchemaRegistryClient is used rather than trying to reach out via HTTP to a schema registry
     *
     * @return DefaultKafkaProducerFactory instance
     */
    @Bean
    public DefaultKafkaProducerFactory<Object, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties(),
                kafkaAvroSerializer(),
                kafkaAvroSerializer()
        );
    }

    /**
     * Configures the kafka consumer factory to use the overridden KafkaAvroSerializer so that the
     * MockSchemaRegistryClient is used rather than trying to reach out via HTTP to a schema registry
     *
     * @return DefaultKafkaConsumerFactory instance
     */
    @Bean
    public DefaultKafkaConsumerFactory<Object, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(),
                kafkaAvroDeserializer(),
                kafkaAvroDeserializer()
        );
    }

    /**
     * Configure the ListenerContainerFactory to use the overridden consumer factory so that the
     * MockSchemaRegistryClient is used under the covers by all consumers when deserializing Avro data.
     *
     * @return ConcurrentKafkaListenerContainerFactory instance
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
