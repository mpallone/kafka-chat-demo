package rganel.demo.bo;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import rganel.demo.domain.KafkaConfig;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class KafkaProducerBo implements MessageProducerBo {
    private final Producer<String, String> producer;
    private final KafkaConfig kafkaConfig;

    public void sendMessage(String message) {
        producer.send(new ProducerRecord<>(kafkaConfig.getTOPIC(), message));
    }

    public void stop() {
        producer.close();
    }
}
