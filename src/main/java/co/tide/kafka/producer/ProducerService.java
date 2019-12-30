package co.tide.kafka.producer;

import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class ProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerService.class);

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    private final String topicName;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Autowired
    public ProducerService(
            final KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${spring.kafka.topic-name}") final String topicName) {

        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    //    the producer picks partition implicitly or explicitly
//    | we can define via the key or via the partition kafka template send | else Round Robin
    @Async
    public void send(final String message) {

        // creating Employee

        Employee employee = new Employee();

//      !NB! not the production way for ID generation ans usage for async service
        employee.setId(atomicInteger.intValue());
        employee.setFirstName("firstName");
        employee.setLastName("lastName");
        employee.setDepartment("IT");
        employee.setDesignation(message);

        // creating partition key for kafka topic
        EmployeeKey employeeKey = new EmployeeKey();
        employeeKey.setId(atomicInteger.getAndIncrement());
        employeeKey.setDepartmentName("IT");

        if (LOG.isDebugEnabled()) {
            LOG.info("sending employee='{}' to topic='{}'", employee, this.topicName);
        }

//        ProducerRecord headers within it | We need to Specify the Partition beforehand if we are to use headers
//    public ProducerRecord(String topic, Integer partition, K key, V value, Iterable<Header> headers) {
//        Header header = new RecordHeader("id", "123".getBytes());
//        Collection headers = Collections.singletonList(header);
//        ProducerRecord<EmployeeKey, Employee> producerRecord = new ProducerRecord(this.topicName,
//                employeeKey, employee, headers);

        ListenableFuture<SendResult<Object, Object>> future = kafkaTemplate.send(this.topicName,
//                new Random().nextInt(4),
                employeeKey, employee);

        future.addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {

            @Override
            public void onSuccess(final SendResult<Object, Object> message) {
                LOG.info("sent message={}  with offset={}", message, message.getRecordMetadata().offset());
            }

            //            Consider DLQ | ErrorQ Implementation
            @Override
            public void onFailure(final Throwable throwable) {
                LOG.error("unable to send message={} due to={}", message, throwable.getCause());
            }
        });
    }
}
