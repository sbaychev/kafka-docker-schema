package co.tide.kafka.consumer;

import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import java.util.stream.StreamSupport;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);

    @KafkaListener(topics = "${spring.kafka.topic-name}",
            clientIdPrefix = "tide.co-avro",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsEmployee(
            ConsumerRecord<EmployeeKey, Employee> employeeConsumerRecord,
            @Payload Employee payload) {

        LOG.info("Logger 1 [] received key {}: Type [{}] | Payload: {} | Record: {}",
                employeeConsumerRecord.key(),
                typeIdHeader(employeeConsumerRecord.headers()),
                payload,
                employeeConsumerRecord);

        //consider manual ack

    }

    private static String typeIdHeader(Headers headers) {

        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals("__TypeId__"))
                .findFirst().map(header -> new String(header.value()))
                .orElse("N/A");
    }
}
