package io.tech.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "index") // Maps to "index" collection in MongoDB
@Data // Lombok: generates getters, setters, toString, equals, hashCode
public class IndexModel {

    @Id // Marks 'id' as the primary key in MongoDB
    private String id;

    private String message; // A sample field to store data
}
