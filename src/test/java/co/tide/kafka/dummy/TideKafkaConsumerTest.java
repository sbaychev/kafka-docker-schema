package co.tide.kafka.dummy;

import co.tide.kafka.config.IntegrationTestConfig;
import co.tide.kafka.config.UnitTestConfig;
import co.tide.kafka.config.UnitTestConfig.KafkaListenerTestHarness;
import co.tide.kafka.consumer.ConsumerService;
import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.test.mockito.LatchCountDownAndCallRealMethodAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Listener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getSingleRecord;

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
        topics = TideKafkaConsumerTest.TEST_CONSUMER_TOPIC)
@DirtiesContext
public class TideKafkaConsumerTest {

    static final String TEST_CONSUMER_TOPIC = "consumer.t";

    private ConsumerService consumerService;

    private KafkaTemplate<Employee, EmployeeKey> template;

    @Autowired
    private KafkaListenerTestHarness harness;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @BeforeEach
    public void setUp() {

        // set up the Kafka producer properties
        Map<String, Object> producerProperties = KafkaTestUtils.producerProps(embeddedKafka);

        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class);
        producerProperties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "mock://localhost:8081");
        producerProperties.put(ProducerConfig.ACKS_CONFIG,
                "all");

        // create a Kafka producer factory
        ProducerFactory<Employee, EmployeeKey> producerFactory =
                new DefaultKafkaProducerFactory<Employee, EmployeeKey>(
                        producerProperties);

        // create a Kafka template
        template = new KafkaTemplate<Employee, EmployeeKey>(producerFactory);

        // set the default topic to send to
        template.setDefaultTopic(TEST_CONSUMER_TOPIC);

        // wait until the partitions are assigned
        kafkaListenerEndpointRegistry
                .getListenerContainers()
                .forEach(messageListenerContainer -> ContainerTestUtils.waitForAssignment(messageListenerContainer,
                        embeddedKafka.getPartitionsPerTopic()));

        consumerService = mock(ConsumerService.class);

    }

    @Test
    public void testReceive() throws InterruptedException {

        Listener listener = this.harness.getSpy("0");

        assertNotNull(listener);

        LatchCountDownAndCallRealMethodAnswer answer = new LatchCountDownAndCallRealMethodAnswer(1);

//        doAnswer(answer).when(listener).listenAsEmployee(any(Employee.class));
//
//        this.template.convertAndSend(this.queue2.getName(), "bar");
//        this.template.convertAndSend(this.queue2.getName(), "baz");
//
//        assertTrue(answer.getLatch().await(10, TimeUnit.SECONDS));
//
//        verify(listener).foo("bar", this.queue2.getName());
//        verify(listener).foo("baz", this.queue2.getName());

        // send the message
        EmployeeKey employeeKey = new EmployeeKey();
        employeeKey.setId(0);
        employeeKey.setDepartmentName("IT");

//        when(consumerService.listenAsEmployee(2L);)

        ArgumentCaptor<Employee> employeeArgumentCaptorCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<ConsumerRecord> consumerRecordArgumentCaptor = ArgumentCaptor.forClass(ConsumerRecord.class);

        List<Employee> employeeList = employeeArgumentCaptorCaptor.getAllValues();

        verify(consumerService)
                .listenAsEmployee(consumerRecordArgumentCaptor.capture(), employeeArgumentCaptorCaptor.capture());

//        assertEquals(Arrays.asList()), employeeList);

        template.sendDefault(employeeKey);

//        kafkaListenerEndpointRegistry.getAllListenerContainers().


    }
}
