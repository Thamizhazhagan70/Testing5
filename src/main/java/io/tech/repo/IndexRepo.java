package io.tech.repo;

import io.tech.model.IndexModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IndexRepo extends MongoRepository<IndexModel, String> {
}
