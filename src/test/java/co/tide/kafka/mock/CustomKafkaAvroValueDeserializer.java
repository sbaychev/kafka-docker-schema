package co.tide.kafka.mock;

import co.tide.kafka.schema.Employee;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

/**
 * This code is not thread safe and should not be used in production environment
 */
public class CustomKafkaAvroValueDeserializer extends KafkaAvroDeserializer {

    @Override
    public Object deserialize(String topic, byte[] bytes) {

        if (topic.equals("event.t")) {
            this.schemaRegistry = getMockClient(Employee.SCHEMA$);
        }

        return super.deserialize(topic, bytes);
    }

    private static SchemaRegistryClient getMockClient(final Schema schema$) {

        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };

    }
}
