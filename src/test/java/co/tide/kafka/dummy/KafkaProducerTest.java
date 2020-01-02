package co.tide.kafka.dummy;

import co.tide.kafka.AKafkaBaseTest;
import co.tide.kafka.mock.CustomKafkaAvroSerializer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KafkaProducerTest {

    MockProducer<?, ?> producer;

    @Before
    public void setUp() {
        producer = new MockProducer<>(
                true, new CustomKafkaAvroSerializer(), new CustomKafkaAvroSerializer());
    }

    @Test
    public void testProducer() throws IOException {

        producer.send(new ProducerRecord("event.t",
                AKafkaBaseTest.createEmployeeKeyAvroPayload(),
                AKafkaBaseTest.createEmployeeAvroPayload()));

        List<? extends ProducerRecord<?, ?>> history = producer.history();

        List<ProducerRecord<String, String>> expected = Arrays.asList(
                new ProducerRecord<String, String>("my_topic", "mykey", "myvalue0"),
                new ProducerRecord<String, String>("my_topic", "mykey", "myvalue1"),
                new ProducerRecord<String, String>("my_topic", "mykey", "myvalue2"),
                new ProducerRecord<String, String>("my_topic", "mykey", "myvalue3"),
                new ProducerRecord<String, String>("my_topic", "mykey", "myvalue4"));

        Assert.assertNotEquals("Sent didn't match expected", expected, history);
    }
}
