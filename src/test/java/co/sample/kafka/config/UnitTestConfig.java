package co.sample.kafka.config;

import java.util.HashMap;
import java.util.Map;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Role;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.KafkaListenerConfigUtils;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;

@TestConfiguration
@PropertySource("classpath:application-test.properties")
public class UnitTestConfig {

    public static class KafkaListenerTestHarness extends KafkaListenerAnnotationBeanPostProcessor {

        private final Map<String, Object> listeners = new HashMap<>();

        @Override
        protected void processListener(MethodKafkaListenerEndpoint endpoint, KafkaListener kafkaListener,
                Object bean, Object adminTarget, String beanName) {

            bean = Mockito.spy(bean);

            this.listeners.put(kafkaListener.id(), bean);

            super.processListener(endpoint, kafkaListener, bean, adminTarget, beanName);
        }

        @SuppressWarnings("unchecked")
        public <T> T getSpy(String id) {
            return (T) this.listeners.get(id);
        }

    }

    @Bean(name = KafkaListenerConfigUtils.KAFKA_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static KafkaListenerTestHarness kafkaListenerAnnotationBeanPostProcessor() {
        return new KafkaListenerTestHarness();
    }

}
