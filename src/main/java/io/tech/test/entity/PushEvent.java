package io.tech.test.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "git_push_events")
public class PushEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String branch;
    private String beforeCommit;
    private String afterCommit;
    private String ticketId;
    private String committerName;
    private String committerEmail;
    private String committerUsername;
    private Long repositoryId;
    private String repositoryName;
    private Boolean repositoryPrivate;

    private String repoOwnerLogin;
    private String repoOwnerEmail;
    private String repoUrl;

    private String pusherName;
    private String pusherEmail;

    private String senderLogin;
    private Long senderId;

    private Boolean created;
    private Boolean deleted;
    private Boolean forced;

    private String compareUrl;
    private Integer commitCount;

    private String headCommitId;

    @Column(columnDefinition = "TEXT")
    private String headCommitMessage;

    private LocalDateTime headCommitTimestamp;
}
