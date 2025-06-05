package io.tech.test.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.tech.test.entity.GitCommitEvent;
import java.util.Optional;

@Repository 
public interface PushEventRepository extends MongoRepository<GitCommitEvent, String> {

	Optional<GitCommitEvent> findByBranch(String branchName);
}
