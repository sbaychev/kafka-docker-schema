package co.tide.kafka;

import co.tide.kafka.config.IntegrationTestConfig;
import co.tide.kafka.config.MockSerdeConfig;
import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Getter
@Setter
@DirtiesContext
@Category(IntegrationTestConfig.class)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Import({IntegrationTestConfig.class, MockSerdeConfig.class})
@EmbeddedKafka(topics = "${spring.kafka.topic-name}")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public abstract class AKafkaBaseTest {

    private String propertiesKeyDeserializer;
    private String propertiesValueDeserializer;
    private String autoOffsetReset;
    private String groupId;
    private String clientId;
    private boolean propertiesSpecificAvroReader;
    private boolean propertiesEnableAutoCommit;
    private String propertiesAutoCommitIntervalMs;
    private int propertiesPollTimeout;

    @Value("${spring.kafka.topic-name}")
    private String topicName;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaURL;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaProperties kafkaProperties;

    protected Producer<EmployeeKey, Employee> employeeKeyEmployeeProducer;
    protected Consumer<EmployeeKey, Employee> employeeKeyEmployeeConsumer;

    @Before
    public void setUp() {

        Map<String, Object> configs = new HashMap<>(
                KafkaTestUtils.consumerProps(getGroupId(), "false", embeddedKafkaBroker));
        configs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                getSchemaURL());
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                io.confluent.kafka.serializers.KafkaAvroDeserializer.class);

//        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
//                getPropertiesKeyDeserializer());
//        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
//                getPropertiesValueDeserializer());
//        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
//                getAutoOffsetReset());
//        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
//                getPropertiesAutoCommitIntervalMs());


        employeeKeyEmployeeConsumer = new DefaultKafkaConsumerFactory<EmployeeKey, Employee>(configs)
                .createConsumer(getGroupId(), getClientId());

        kafkaProperties.buildConsumerProperties();

        employeeKeyEmployeeConsumer.subscribe(Lists.newArrayList(getTopicName()));

    }

    @After
    public void reset() {

        //consumers needs to be closed because new one are created before every test
        employeeKeyEmployeeConsumer.close();
    }

//    public static Employee createEmployeeAvroPayload() throws IOException {
//
//        //Create schema from .avsc file
//        Schema mainSchema = new Schema.Parser().parse(new ClassPathResource("employee-schema.avsc").getInputStream());
//
//        //Create avro message with defined schema
//        Employee avroMessage = new SpecificData.(.mainSchema);
//
//        //Populate avro message
//        avroMessage.put("first_name", "Karen");
//        avroMessage.put("", "");
//
//        return avroMessage;
//    }
}