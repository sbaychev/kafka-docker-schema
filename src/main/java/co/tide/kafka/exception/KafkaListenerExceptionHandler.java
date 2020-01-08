package co.tide.kafka.exception;

import co.tide.kafka.producer.ProducerController;
import org.apache.kafka.clients.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service("myExceptionHandler")
public class KafkaListenerExceptionHandler implements KafkaListenerErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaListenerExceptionHandler.class);

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        LOG.info(message.getPayload().toString());
        return null;
    }
    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception, Consumer<?, ?> consumer) {
        LOG.info(message.getPayload().toString());
        return null;
    }
}
