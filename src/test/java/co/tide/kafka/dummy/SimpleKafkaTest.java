package co.tide.kafka.dummy;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@EmbeddedKafka
@ActiveProfiles("test")
public class SimpleKafkaTest {

    private static final String TOPIC = "domain-events";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<?, ?> consumer;

    @Before
    public void setUp() {

        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));

        consumer = new DefaultKafkaConsumerFactory(configs, new StringDeserializer(), new StringDeserializer()).createConsumer();
        consumer.subscribe(Collections.singleton(TOPIC));
        consumer.poll(0);
    }

    @After
    public void tearDown() {
        consumer.close();
    }

    @Test
    public void kafkaSetup_withTopic_ensureSendMessageIsReceived() {

        // Arrange
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();

        // Act
        producer.send(new ProducerRecord<>(TOPIC, "my-aggregate-id", "{\"event\":\"Test Event\"}"));
        producer.flush();

        // Assert
        ConsumerRecord<?, ?> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
        assertThat(singleRecord).isNotNull();
        assertThat(singleRecord.key()).isEqualTo("my-aggregate-id");
        assertThat(singleRecord.value()).isEqualTo("{\"event\":\"Test Event\"}");
    }

}
