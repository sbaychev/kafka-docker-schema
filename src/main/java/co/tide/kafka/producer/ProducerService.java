package co.tide.kafka.producer;

import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import java.util.stream.IntStream;
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

    @Autowired
    public ProducerService(
            final KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${spring.kafka.topic-name}") final String topicName) {

        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @Async
    public void send(final String message) {

        // creating Employee

        Employee employee = new Employee();

        employee.setId(1);
        employee.setFirstName("firstName");
        employee.setLastName("lastName");
        employee.setDepartment("IT");
        employee.setDesignation(message);

        // creating partition key for kafka topic
        EmployeeKey employeeKey = new EmployeeKey();
        employeeKey.setId(1);
        employeeKey.setDepartmentName("IT");

        LOG.info("sending employee='{}' to topic='{}'", employee, this.topicName);
//        ProducerRecord headers within it

        IntStream.range(0, 10)
                .forEach(i -> kafkaTemplate.send(this.topicName, employeeKey, employee));

//        ListenableFuture<SendResult<Object, Object>> future = kafkaTemplate.send(this.topicName, employeeKey, employee);
//
//        future.addCallback(new ListenableFutureCallback<SendResult<Object, Object>>() {
//
//            @Override
//            public void onSuccess(final SendResult<Object, Object> message) {
//                LOG.info("sent message={}  with offset={}", message, message.getRecordMetadata().offset());
//            }
//
//            //            Consider DLQ Implementation
//            @Override
//            public void onFailure(final Throwable throwable) {
//                LOG.error("unable to send message={} due to={}", message, throwable.getCause());
//            }
//        });
    }
}
