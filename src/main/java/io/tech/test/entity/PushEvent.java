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
@Document(collection = "git_push_event")
public class PushEvent {

    @Id
    private String id; // Unique identifier for the push event

    private String branch;                // The branch that was pushed
    private String commitId;              // The ID of the commit
    private String ticketId;              // The ticket ID associated with the commit
    private String committerName;         // The name of the committer
    private String committerEmail;        // The email of the committer
    private String committerUsername;     // The username of the committer
    private Long repositoryId;            // The ID of the repository
    private String repositoryName;        // The name of the repository
    private Boolean repositoryPrivate;     // Indicates if the repository is private

    private String repoOwnerLogin;        // The login of the repository owner
    private String repoOwnerEmail;        // The email of the repository owner
    private String repoUrl;               // The URL of the repository

    private String pusherName;            // The name of the pusher
    private String pusherEmail;           // The email of the pusher

    private String senderLogin;           // The login of the sender
    private Long senderId;                // The ID of the sender

    private Boolean created;               // Indicates if the event was created
    private Boolean deleted;               // Indicates if the event was deleted
    private Boolean forced;                // Indicates if the push was forced
    private String commitUrl;             // The URL of the commit
    private Integer commitCount;          // The number of commits in the push
    private String headCommitId;          // The ID of the head commit
    private List<String> messages;        // List of commit messages
    private LocalDateTime headCommitTimestamp; // Timestamp of the head commit

    private String sourceBranch;          // The source branch for the pull request
    private String targetBranch;          // The target branch for the pull request
    private String mergedAt;              // The date when the pull request was merged
    private String pullRequestUrl;        // The URL of the pull request
}
