package co.tide.kafka.consumer;

import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
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

    private Map<String, LinkedBlockingQueue<Employee>> employeeMapEvents = new ConcurrentHashMap<>();

    //  We can have multiple listeners per given partition thus utilizing the kafkaListenerContainerFactory proper
//    topicPartitions = { @TopicPartition(topic = "${spring.kafka.topic-name}", partitions = { "0" }) })
    @KafkaListener(topics = "${spring.kafka.topic-name}",
            clientIdPrefix = "tide.co-avro",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsEmployee(
            ConsumerRecord<EmployeeKey, Employee> employeeConsumerRecord,
            @Payload Employee payload) {

//        LinkedBlockingQueue<Employee> queue = employeeMapEvents.get(payload.getDepartment());
//        if (queue == null) {
//            queue = new LinkedBlockingQueue<>();
//            queue.add(payload);
//            employeeMapEvents.put(payload.getDepartment().toString(), queue);
//        } else {
//            queue.add(payload);
//        }

        System.out.println(
                "I received message on thread: " + Thread.currentThread().getName() +
                        " hashcode: " + Thread.currentThread().hashCode());

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
