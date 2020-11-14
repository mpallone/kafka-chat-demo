package rganel.demo.domain;

import com.google.inject.Inject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class KafkaConfig {
    private final String TOPIC;
    private final String URI;
}
