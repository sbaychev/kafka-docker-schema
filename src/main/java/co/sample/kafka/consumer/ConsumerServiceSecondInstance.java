package co.sample.kafka.consumer;

import co.sample.kafka.schema.Employee;
import co.sample.kafka.schema.EmployeeKey;
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

/**
 * Sample Service with 4 partition consumers (we have set the to be consumed topic to have 4 partitions) that do the
 * same work in essence, but in parallel
 */

@Service
public class ConsumerServiceSecondInstance implements ConsumerSeekAware {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerServiceSecondInstance.class);

    private Map<String, LinkedBlockingQueue<Employee>> employeeMapEvents = new ConcurrentHashMap<>();

    @KafkaListener(
            //            topicPartitions = {@TopicPartition(topic = "${spring.kafka.topic-name}", partitions = {"0"})}
            topics = "${spring.kafka.topic-name}",
            clientIdPrefix = "sample.co-avro-0",
            id = "111",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory",
            errorHandler = "myExceptionHandler"
    )
    public void listenAsEmployee111(
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

//        LOG.info("Logger 0 [] I received message on thread: " + Thread.currentThread().getName() +
//                " hashcode: " + Thread.currentThread().hashCode());

        LOG.info("Logger 111 [] received key {}: Type [{}] | Payload: {} | Record: {}",
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

    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        // register custom callback
        // NOOP
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        // Seek all the assigned partition to a certain offset | Manual Offset for Given Consumer
        // -> in this case beginning on app startup - would process it
        for (TopicPartition topicPartition : assignments.keySet()) {
            callback.seekToBeginning(topicPartition.topic(), topicPartition.partition());
        }

    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        // executes when the Kafka container is idle
        // NOOP
    }
}
