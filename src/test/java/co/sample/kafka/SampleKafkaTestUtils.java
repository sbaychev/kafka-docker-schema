package co.sample.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

public class SampleKafkaTestUtils {

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
