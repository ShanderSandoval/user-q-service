package yps.systems.ai.service;

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
    public void listen(@Payload Object payload, @Header("eventType") String eventType, @Header("source") String source) {
        switch (eventType) {
            case "CREATE_USER":
                if (payload instanceof User user) {
                    System.out.println("Processing CREATE_USER event from " + source);
                    userRepository.save(user);
                }
                break;
            case "UPDATE_USER":
                if (payload instanceof User user) {
                    System.out.println("Processing UPDATE_USER event from " + source);
                    Optional<User> userOptional = userRepository.findById(user.getId());
                    if (userOptional.isPresent()) {
                        userOptional.get().setId(user.getId());
                        userRepository.save(userOptional.get());
                    }
                }
                break;
            case "DELETE_USER":
                if (payload instanceof String userElementId) {
                    System.out.println("Processing DELETE_USER event from " + source);
                    Optional<User> userOptional = userRepository.findById(userElementId);
                    userOptional.ifPresent(value -> userRepository.deleteById(value.getId()));
                }
                break;
            default:
                System.out.println("Unknown event type: " + eventType);
        }
    }

}
