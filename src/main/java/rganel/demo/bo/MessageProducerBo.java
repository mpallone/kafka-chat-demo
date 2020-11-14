package rganel.demo.bo;

public interface MessageProducerBo {

    void sendMessage(String message);

    void stop();
}
