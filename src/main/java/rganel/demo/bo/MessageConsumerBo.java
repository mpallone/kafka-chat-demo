package rganel.demo.bo;

import java.util.List;

public interface MessageConsumerBo extends Runnable {
    
    List<String> getMessages();
    
    void stop();
}
