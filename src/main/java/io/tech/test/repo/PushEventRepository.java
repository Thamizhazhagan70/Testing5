package io.tech.test.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import io.tech.test.entity.GitCommitEvent;

import java.util.List;
import java.util.Optional;

@Repository 
public interface PushEventRepository extends MongoRepository<GitCommitEvent, String> {

	List<GitCommitEvent> findByBranch(String branchName);

	List<GitCommitEvent>  findByBranchAndTicketId(String branch, String ticketId);


}
