package io.tech.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "commit_details")
public class GitCommitEvent {

    @Id
    private String id; //
    private String commitId;//
    private String ticketId;//
    private String repoUrl;//
    private String branch;//
    private String pusherName;
    private String pusherEmail;
    private Integer commitCount;
    private String fieldValueId;
    private String projectId;
    private String message;
    private String url;
    private String authorName;
    private String authorEmail;
    private String username;
    private LocalDateTime updatedDate;
    private String repoName;//
    private String repoFullName;//
    private boolean committedByBot;
}
