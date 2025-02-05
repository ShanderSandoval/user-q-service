package yps.systems.ai.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yps.systems.ai.model.User;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {
}
