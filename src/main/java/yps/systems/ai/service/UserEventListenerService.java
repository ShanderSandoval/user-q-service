package yps.systems.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import yps.systems.ai.model.User;
import yps.systems.ai.repository.IUserRepository;

import java.util.Optional;

@Service
public class UserEventListenerService {

    private final IUserRepository userRepository;

    @Autowired
    public UserEventListenerService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${env.kafka.topicEvent}")
    public void listen(@Payload String payload, @Header("eventType") String eventType, @Header("source") String source) {
        System.out.println("Processing " + eventType + " event from " + source);
        switch (eventType) {
            case "CREATE_USER":
                try {
                    User user = new ObjectMapper().readValue(payload, User.class);
                    userRepository.save(user);
                } catch (JsonProcessingException e) {
                    System.err.println("Error parsing Person JSON: " + e.getMessage());
                }
                break;
            case "UPDATE_USER":
                try {
                    User user = new ObjectMapper().readValue(payload, User.class);
                    Optional<User> userOptional = userRepository.findById(user.getId());
                    userOptional.ifPresent(userRepository::save);
                } catch (JsonProcessingException e) {
                    System.err.println("Error parsing Person JSON: " + e.getMessage());
                }
            case "DELETE_USER":
                Optional<User> userOptional = userRepository.findById(payload.replaceAll("\"", ""));
                userOptional.ifPresent(value -> userRepository.deleteById(value.getId()));
                break;
            default:
                System.out.println("Unknown event type: " + eventType);
        }
    }

}
