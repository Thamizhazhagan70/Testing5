package io.tech.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pull_request_details")
public class PullRequestDetail {
    @Id
    private String id;

    @Indexed(unique = true)
    private String pullRequestId;
    private String Message;
    private String pullRequestUrl;
    private String status; // open, closed, merged
    private String ticketId;
    private String sourceBranch;
    private String targetBranch;
    private String projectId;
    private String FieldvalueId;
    private String createdBy;
    private String requestedReviewer;
    private String merged_By;
    private String createdDate;
    private String mergedDate;
    private String repoName;
    private String repoFullName;
    private Boolean isMerged;
    
}
