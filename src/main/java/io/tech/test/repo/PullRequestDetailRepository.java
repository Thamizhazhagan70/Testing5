package io.tech.test.repo;

import io.tech.test.entity.PullRequestDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullRequestDetailRepository extends MongoRepository<PullRequestDetail, String> {
    // Additional query methods can be defined here if needed
}
