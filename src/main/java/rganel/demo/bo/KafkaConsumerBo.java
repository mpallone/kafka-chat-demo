package rganel.demo.bo;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class KafkaConsumerBo implements MessageConsumerBo {
    private final Consumer<String, String> consumer;
    @Getter private final ArrayList<String> messages = new ArrayList<>();
    
    @Override
    public void run() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                String message = record.value();
                messages.add(message);
            }
            consumer.commitAsync();
        }
    }
    
    public void stop() {
        consumer.close();
    }
}
