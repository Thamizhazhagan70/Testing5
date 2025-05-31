package io.tech.test.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.tech.test.entity.PushEvent;

@Repository 
public interface PushEventRepository extends MongoRepository<PushEvent, Long> {
}
