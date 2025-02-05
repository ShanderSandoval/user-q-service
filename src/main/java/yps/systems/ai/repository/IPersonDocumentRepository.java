package yps.systems.ai.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yps.systems.ai.model.Person;

@Repository
public interface IPersonDocumentRepository extends MongoRepository<Person, String> {
}
