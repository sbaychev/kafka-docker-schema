package co.sample.kafka.dummy;

import co.sample.kafka.config.UnitTestConfig;
import co.sample.kafka.producer.ProducerService;
import co.sample.kafka.schema.Employee;
import co.sample.kafka.schema.EmployeeKey;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = {
                KafkaAutoConfiguration.class,//FIXME: do not need it, find out why spring throws exceptions without
                UnitTestConfig.class
        }
)
@EmbeddedKafka(
        partitions = 1,
        topics = SampleKafkaProducerTest.TEST_PRODUCER_TOPIC)
@DirtiesContext
public class SampleKafkaProducerTest {

    static final String TEST_PRODUCER_TOPIC = "producer.t";

    private ProducerService producerService;

    private KafkaMessageListenerContainer<EmployeeKey, Employee> container;

    private BlockingQueue<ConsumerRecord<EmployeeKey, Employee>> records;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @BeforeEach
    public void setUp() {

        // set up the Kafka consumer properties
        Map<String, Object> consumerProperties =
                KafkaTestUtils.consumerProps("sender", "false",
                        embeddedKafka);
        consumerProperties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "mock://localhost:8081");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                KafkaAvroDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                KafkaAvroDeserializer.class);
        consumerProperties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
                true);

        // create a Kafka consumer factory
        DefaultKafkaConsumerFactory<EmployeeKey, Employee> consumerFactory =
                new DefaultKafkaConsumerFactory<>(
                        consumerProperties);

        // set the topic that needs to be consumed
        ContainerProperties containerProperties =
                new ContainerProperties(TEST_PRODUCER_TOPIC);

        // create a Kafka MessageListenerContainer
        container = new KafkaMessageListenerContainer<>(consumerFactory,
                containerProperties);

        // create a thread safe queue to store the received message
        records = new LinkedBlockingQueue<>();

        // setup a Kafka message listener
        container.setupMessageListener((MessageListener<EmployeeKey, Employee>) record -> records.add(record));

        // start the container and underlying message listener
        container.start();

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container,
                embeddedKafka.getPartitionsPerTopic());

        Map<String, Object> producerProperties = KafkaTestUtils.producerProps(embeddedKafka);

        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class);
        producerProperties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "mock://localhost:8081");
        producerProperties.put(ProducerConfig.ACKS_CONFIG,
                "all");

        KafkaTemplate kafkaTemplate = new KafkaTemplate(
                new DefaultKafkaProducerFactory<>(producerProperties));
        
        producerService = new ProducerService(kafkaTemplate, TEST_PRODUCER_TOPIC);
    }

    @AfterEach
    public void tearDown() {
        // stop the container
        container.stop();
    }

    @Test
    public void testSend() throws InterruptedException {

        // send the message
        String designation = "Time Lord";
        producerService.send(designation);

        EmployeeKey employeeKey = new EmployeeKey();
        employeeKey.setId(0);
        employeeKey.setDepartmentName("IT");

        // check that the message was received
        ConsumerRecord<EmployeeKey, Employee> received =
                records.poll(2, TimeUnit.SECONDS);

        Assert.assertNotNull(received);

        assertEquals(designation, received.value().getDesignation().toString());

        assertEquals(received.key(), employeeKey);
    }
}
