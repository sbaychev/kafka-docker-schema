package co.tide.kafka.producer;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);
    private AtomicInteger counter = new AtomicInteger();
    private final ProducerService producerService;

    @Autowired
    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {

        LOG.info("incoming message='{}' ", message);

            IntStream.range(0, 10)
                    .forEach(i -> producerService.send(message + counter.getAndIncrement()));

//        producerService.send(message);
    }

}
