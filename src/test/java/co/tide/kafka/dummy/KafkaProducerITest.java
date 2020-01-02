package co.tide.kafka.dummy;

import co.tide.kafka.AKafkaBaseTest;
import co.tide.kafka.producer.ProducerService;

import java.io.IOException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecordTest;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class KafkaProducerITest extends AKafkaBaseTest {

    @Autowired
    private ProducerService mockProducerService;

    private static final String MESSAGE = "IT";

//    @org.junit.Test
//    public void should_send() throws IOException {
//
//        ArgumentCaptor<ProducerRecordTest> captor = ArgumentCaptor.forClass(ProducerRecordTest.class);
//        mockProducerService.send("1");
//
//        ProducerRecordTest actualRecord = captor.getValue();
//
////        verify(mockProducerService).send(captor.capture());
////        assertThat(actualRecord.topic()).isEqualTo("mock topic");
////        assertThat(actualRecord.key()).isEqualTo(0);
//
//        //Convert avro message from avro schema message to POJO
////        Employee employee = mapRecordToObject(createEmployeeAvroPayload(), new Employee());
////
////        this.mockProducerService.send(MESSAGE);
////
////        kafkaTemplate.send(getTopicName(), employee);
////
////        ConsumerRecord<EmployeeKey, Employee> singleRecord = KafkaTestUtils
////                .getSingleRecord(employeeKeyEmployeeConsumer, getTopicName(), 1500);
////
////        Assertions.assertThat(singleRecord).isNotNull();
//    }

    @org.junit.Test
    public void should_send() {

        this.mockProducerService.send(MESSAGE);

        ConsumerRecord<Object, Object> singleRecord = KafkaTestUtils
                .getSingleRecord(employeeKeyEmployeeConsumer, getTopicName());

        assertNotNull(singleRecord);


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
