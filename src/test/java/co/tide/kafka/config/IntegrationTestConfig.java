package co.tide.kafka.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@TestConfiguration
@Import({MockSerdeConfig.class})
@PropertySource("classpath:application-test.properties")
public class IntegrationTestConfig {

//    @MockBean
//    private Supplier dummyBean; // TODO: remove me
}
