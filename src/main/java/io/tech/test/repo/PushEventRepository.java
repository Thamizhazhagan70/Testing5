package io.tech.test.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.tech.test.entity.PushEvent;
import java.util.Optional;

@Repository 
public interface PushEventRepository extends MongoRepository<PushEvent, String> {

	Optional<PushEvent> findByBranch(String branchName);
}
