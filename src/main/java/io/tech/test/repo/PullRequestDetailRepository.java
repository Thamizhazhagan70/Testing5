package io.tech.test.repo;

import io.tech.test.entity.PullRequestDetail;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullRequestDetailRepository extends MongoRepository<PullRequestDetail, String> {

	List<PullRequestDetail> findByPullRequestId(String prId);
    // Additional query methods can be defined here if needed

	List<PullRequestDetail> findBySourceBranch(String sourceBranch);
}
