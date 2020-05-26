//package co.sample.kafka;
//
//import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
//import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
//import java.util.Map;
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.test.IntegrationTest;
//import org.assertj.core.util.Lists;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.experimental.categories.Category;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.test.EmbeddedKafkaBroker;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@Getter
//@Setter
//@DirtiesContext
//@ActiveProfiles("test")
//@SpringBootTest(classes = {
//        ProducerConsumerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@RunWith(SpringRunner.class)
//@Category(IntegrationTest.class)
////@Import({IntegrationTestConfig.class})
//@EmbeddedKafka(topics = "${spring.kafka.topic-name}")
//@EnableConfigurationProperties
//@ConfigurationProperties(prefix = "spring.kafka.consumer")
//public abstract class AKafkaBaseTest {
//
//    private String autoOffsetReset;
//    private String groupId;
//    private String clientId;
//    private boolean propertiesSpecificAvroReader;
//    private boolean propertiesEnableAutoCommit;
//    private String propertiesAutoCommitIntervalMs;
//
//    private static String TOPIC = "event.t";
//
//    @Value("${spring.kafka.producer.properties-acks}")
//    private String producerPropertiesAcks;
//
//    @Value("${spring.kafka.messages-per-request}")
//    private int maxMessagesPerRequest;
//
//    @Value("${spring.kafka.topic-name}")
//    private String topicName;
//
//    @Value("${spring.kafka.properties.schema.registry.url}")
//    private String schemaURL;
//
//    @Autowired
//    private EmbeddedKafkaBroker embeddedKafka;
//
//    @Autowired
//    private KafkaProperties kafkaProperties;
//
//    protected Producer<Object, Object> employeeKeyEmployeeProducer;
//    protected Consumer<Object, Object> employeeKeyEmployeeConsumer;
//
//    @Before
//    public void setUp() {
//
//        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
//        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
//
//        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                embeddedKafka.getBrokersAsString());
//        consumerProperties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
//                getSchemaURL());
//        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                CustomKafkaAvroKeyDeserializer.class);
//        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                CustomKafkaAvroValueDeserializer.class);
//        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
//                getAutoOffsetReset());
//        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
//                isPropertiesEnableAutoCommit());
//        consumerProperties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
//                isPropertiesSpecificAvroReader());
//        consumerProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
//                getPropertiesAutoCommitIntervalMs());
//        consumerProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
//                getMaxMessagesPerRequest());
//
//        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                embeddedKafka.getBrokersAsString());
//        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                CustomKafkaAvroSerializer.class);
//        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
//                CustomKafkaAvroSerializer.class);
//        producerProperties.put(ProducerConfig.ACKS_CONFIG,
//                getProducerPropertiesAcks());
//
//        employeeKeyEmployeeConsumer = new DefaultKafkaConsumerFactory<>(consumerProperties)
//                .createConsumer(getGroupId(), getClientId());
//
//        employeeKeyEmployeeConsumer.subscribe(Lists.newArrayList(getTopicName()));
//
//        employeeKeyEmployeeProducer = new KafkaProducer<>(producerProperties);
//
//    }
//
//    @After
//    public void reset() {
//
//        //consumers needs to be closed because new one are created before every test
//        employeeKeyEmployeeConsumer.close();
//    }
//}