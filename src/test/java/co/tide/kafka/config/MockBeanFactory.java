//package co.tide.kafka.config;
//
//import co.tide.kafka.consumer.ConsumerService;
//import co.tide.kafka.producer.ProducerService;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import spock.mock.DetachedMockFactory;
//
//@TestConfiguration
//class MockBeanFactory {
//
//    private DetachedMockFactory mockFactory;
//
//    @Bean
//    @Primary
//    public ProducerService producerServiceMock() {
//        return mockFactory.Mock(ProducerService.class);
//    }
//
//    @Bean
//    @Primary
//    public ConsumerService consumerServiceMock() {
//        return mockFactory.Mock(ConsumerService.class);
//    }
//}
