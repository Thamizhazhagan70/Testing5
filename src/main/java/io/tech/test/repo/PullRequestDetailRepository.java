package io.tech.test.repo;

import io.tech.test.entity.PullRequestDetail;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullRequestDetailRepository extends MongoRepository<PullRequestDetail, String> {

	Optional<PullRequestDetail> findByPullRequestId(String prId);
    // Additional query methods can be defined here if needed
}
