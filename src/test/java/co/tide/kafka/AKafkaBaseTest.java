package co.tide.kafka;

import co.tide.kafka.config.IntegrationTestConfig;
import co.tide.kafka.mock.CustomKafkaAvroKeyDeserializer;
import co.tide.kafka.mock.CustomKafkaAvroSerializer;
import co.tide.kafka.mock.CustomKafkaAvroValueDeserializer;
import co.tide.kafka.schema.EmployeeKey;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.test.IntegrationTest;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@Getter
@Setter
@DirtiesContext
@ActiveProfiles("test")
@SpringBootTest(classes = {
        ProducerConsumerApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@Category(IntegrationTest.class)
//@Import({IntegrationTestConfig.class})
@EmbeddedKafka(topics = "${spring.kafka.topic-name}")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public abstract class AKafkaBaseTest {

    private String autoOffsetReset;
    private String groupId;
    private String clientId;
    private boolean propertiesSpecificAvroReader;
    private boolean propertiesEnableAutoCommit;
    private String propertiesAutoCommitIntervalMs;

    private static String TOPIC = "event.t";

    @Value("${spring.kafka.producer.properties-acks}")
    private String producerPropertiesAcks;

    @Value("${spring.kafka.messages-per-request}")
    private int maxMessagesPerRequest;

    @Value("${spring.kafka.topic-name}")
    private String topicName;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaURL;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaProperties kafkaProperties;

    protected Producer<Object, Object> employeeKeyEmployeeProducer;
    protected Consumer<Object, Object> employeeKeyEmployeeConsumer;

    @Before
    public void setUp() {

        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();

        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                embeddedKafka.getBrokersAsString());
        consumerProperties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                getSchemaURL());
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                CustomKafkaAvroKeyDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                CustomKafkaAvroValueDeserializer.class);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                getAutoOffsetReset());
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                isPropertiesEnableAutoCommit());
        consumerProperties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
                isPropertiesSpecificAvroReader());
        consumerProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                getPropertiesAutoCommitIntervalMs());
        consumerProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                getMaxMessagesPerRequest());

        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                embeddedKafka.getBrokersAsString());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                CustomKafkaAvroSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                CustomKafkaAvroSerializer.class);
        producerProperties.put(ProducerConfig.ACKS_CONFIG,
                getProducerPropertiesAcks());

        employeeKeyEmployeeConsumer = new DefaultKafkaConsumerFactory<>(consumerProperties)
                .createConsumer(getGroupId(), getClientId());

        employeeKeyEmployeeConsumer.subscribe(Lists.newArrayList(getTopicName()));

        employeeKeyEmployeeProducer = new KafkaProducer<>(producerProperties);

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

        //Populate avro message
        avroMessage
                .set("id", "1")
                .set("firstName", "Doctor")
                .set("lastName", "Who")
                .set("department", "Tardis")
                .set("designation", "The Doctor");

        return avroMessage.build();
    }

    public static GenericRecord createEmployeeKeyAvroPayload() throws IOException {

        //Create schema from .avsc file
        Schema mainSchema = new Schema.Parser()
                .parse(new ClassPathResource("employee-key-schema.avsc").getInputStream());

        //Create avro message with defined schema
        GenericRecordBuilder avroMessage = new GenericRecordBuilder(mainSchema);

        //Populate avro message
        avroMessage
                .set("id", "1")
                .set("departmentName", "IT");

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

        Assert.isTrue(schema.getFields().equals(record.getSchema().getFields()), "Schema fields don’t match");

        record.getSchema().getFields()
                .forEach(d -> PropertyAccessorFactory
                        .forDirectFieldAccess(object)
                        .setPropertyValue(d.name(),
                                record.get(d.name()) == null ? record.get(d.name()) : record.get(d.name()).toString()));

        return object;
    }
}