package co.tide.kafka.consumer;

import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.StreamSupport;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService implements ConsumerSeekAware {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerService.class);

    private Map<String, LinkedBlockingQueue<Employee>> employeeMapEvents = new ConcurrentHashMap<>();

//    We can have multiple listeners per given partition thus utilizing the kafkaListenerContainerFactory proper
//    Only one consumer in a consumer group (groupId) can consume from a partition at a time - that is the way Kafka works.
//    Consumer can read from more than one partition, but as said above a partition can only be used by one consumer in a
//    consumer group at a time. If you only have one partition, then you can only have one consumer
//    A rebalance occurs whenever a new client connects. Kafka does at least once behavior, and it should be
//    addressed that the messages (record deliveries ) are idempotent
//    topicPartitions = { @TopicPartition(topic = "${spring.kafka.topic-name}", partitions = { "0" }) })
    @KafkaListener(topics = "${spring.kafka.topic-name}",
            clientIdPrefix = "tide.co-avro",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listenAsEmployee(
            ConsumerRecord<EmployeeKey, Employee> employeeConsumerRecord,
            //(required = false) if using compacted topics ergo tombstone record
            @Payload(required = false) Employee payload) {

//        LinkedBlockingQueue<Employee> queue = employeeMapEvents.get(payload.getDepartment());
//        if (queue == null && payload != null) {
//            queue = new LinkedBlockingQueue<>();
//            queue.add(payload);
//            employeeMapEvents.put(payload.getDepartment().toString(), queue);
//        } else if(payload == null) {
//        LOG.info("received employee with id to be deleted {}",  employeeConsumerRecord.key());
//        }
//        else {
//             LOG.info("received employee update {}", payload);
//            queue.add(payload);
//        }

//        LOG.info("I received message on thread: " + Thread.currentThread().getName() +
//                " hashcode: " + Thread.currentThread().hashCode());

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

//    @Override
//    public void registerSeekCallback(ConsumerSeekCallback callback) {
//        // register custom callback
//        // NOOP
//    }
//
//    @Override
//    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
//        // Seek all the assigned partition to a certain offset | Manual Offset for Given Consumer
//        // -> in this case beginning on app startup - would process it
//        for (TopicPartition topicPartition : assignments.keySet()) {
//            callback.seekToBeginning(topicPartition.topic(), topicPartition.partition());
//        }
//
//    }
//
//    @Override
//    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
//        // executes when the Kafka container is idle
//        // NOOP
//    }
}
