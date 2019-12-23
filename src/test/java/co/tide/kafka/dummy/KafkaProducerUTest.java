package co.tide.kafka.dummy;

import co.tide.kafka.AKafkaBaseTest;
import co.tide.kafka.producer.ProducerService;
import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;
import org.opentest4j.AssertionFailedError;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.utils.KafkaTestUtils;

public class KafkaProducerUTest extends AKafkaBaseTest {

    @MockBean
    private ProducerService mockProducerService;

    private static final String MESSAGE = "IT";

    @org.junit.Test
    public void should_send() {

        this.mockProducerService.send(MESSAGE);

        ConsumerRecord<EmployeeKey, Employee> singleRecord = KafkaTestUtils
                .getSingleRecord(employeeKeyEmployeeConsumer, getTopicName(), 1500);

        Assertions.assertThat(singleRecord).isNotNull();
    }

    @org.junit.Test(expected = AssertionFailedError.class)
    public void should_not_send() {

        this.mockProducerService.send(MESSAGE);

        ConsumerRecord<EmployeeKey, Employee> singleRecord = KafkaTestUtils
                .getSingleRecord(employeeKeyEmployeeConsumer, "topic", 1000);

    }

//    @Test
//    public void canSendAndReceiveMessage() throws IOException {
//
//        KafkaProducer<EmployeeKey, Employee> producer = new KafkaProducer<>(producerProps);
//        ProducerRecord<EmployeeKey, Employee> record = new ProducerRecord<>(TOPIC, createUserAvroPayload());
//        producer.send(record);
//
//        KafkaConsumer<EmployeeKey, Employee> consumer = new KafkaConsumer<>(consumerProps);
//        consumer.subscribe(Collections.singletonList(TOPIC));
//
//        ConsumerRecords<EmployeeKey, Employee> message = consumer.poll(5000);
//
//        message.forEach(r -> {
//            String value = r.value().toString();
//            System.out.println(value);
//        });
//    }
}
