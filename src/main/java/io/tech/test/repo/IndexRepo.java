package io.tech.test.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.tech.test.model.IndexModel;

public interface IndexRepo extends MongoRepository<IndexModel, String> {
}
