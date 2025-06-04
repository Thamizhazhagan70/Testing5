package io.tech.test.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commit_events") // Specify the table name if needed
public class CommitEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    private String branch;            // The branch that was pushed
    private String TicketId;
    private String commitId;          // The ID of the commit
    private String authorName;        // The name of the author
    private String authorEmail;       // The email of the author
    private String commitUrl;         // The URL of the commit
    private String Message;     // The message of the commit
    private String repositoryName;    // The name of the repository
    private String pusherName;        // The name of the pusher
    private String pusherEmail;       // The email of the pusher
    private Date committedOn;         // Date of the commit
    private String projectId;         // Project ID
    private String fieldValueId;      // Field value ID
    private String pullRequestUrl;    // URL of the pull request
    private Integer pullRequestNumber; // Pull request number
    @ElementCollection
    private List<String> requestedReviewers; // List of requested reviewers
    private String sourceBranch;      // Source branch for the pull request
    private String targetBranch;      // Target branch for the pull request
    private String state;             // State of the pull request (e.g., "open")
    private String title;             // Title of the pull request (same as commit message)
    private Date createdDate;         // Date created
    private Date updatedDate;         // Date updated
    private Date closedDate;          // Date closed
    private Date mergedDate;          // Date merged
    private Boolean isMerged;          // Indicates if the pull request is merged
    private String mergedBy;          // User who merged the pull request
}
