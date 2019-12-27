package co.tide.kafka;

import co.tide.kafka.config.IntegrationTestConfig;
import co.tide.kafka.config.MockSerdeConfig;
import co.tide.kafka.schema.Employee;
import co.tide.kafka.schema.EmployeeKey;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

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

    public static GenericRecord createEmployeeAvroPayload() throws IOException {

        //Create schema from .avsc file
        Schema mainSchema = new Schema.Parser().parse(new ClassPathResource("employee-schema.avsc").getInputStream());

        //Create avro message with defined schema
        GenericRecordBuilder avroMessage = new GenericRecordBuilder(mainSchema);

        // creating partition key for kafka topic
        EmployeeKey employeeKey = new EmployeeKey();
        employeeKey.setId(1);
        employeeKey.setDepartmentName("IT");

        //Populate avro message
        avroMessage
                .set("id", "1")
                .set("firstName", "Doctor")
                .set("lastName", "Who")
                .set("department", "Tardis")
                .set("designation", "The Doctor");

        return avroMessage.build();
    }

    public static <T> GenericRecord pojoToRecord(T model) throws IOException {

        Schema schema = ReflectData.get().getSchema(model.getClass());

        ReflectDatumWriter<T> datumWriter = new ReflectDatumWriter<>(schema);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
        datumWriter.write(model, encoder);
        encoder.flush();

        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(outputStream.toByteArray(), null);

        return datumReader.read(null, decoder);
    }

    public static <T> T mapRecordToObject(GenericRecord record, T object) {

        Assert.notNull(record, "record must not be null");
        Assert.notNull(object, "object must not be null");

        final Schema schema = ReflectData.get().getSchema(object.getClass());

        Assert.isTrue(schema.getFields().equals(record.getSchema().getFields()), "Schema fields didn’t match");

        record.getSchema().getFields().forEach(d -> PropertyAccessorFactory
                .forDirectFieldAccess(object).setPropertyValue(d.name(),
                        record.get(d.name()) == null ? record.get(d.name()) : record.get(d.name()).toString()));
        return object;
    }
}