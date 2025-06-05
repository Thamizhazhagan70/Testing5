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
    private String title;
    private String url;
    private String state; // open, closed, merged
    private String sourceBranch;
    private String targetBranch;
    private String createdBy;
    private String createdAt;
    private String mergedAt;
    private String repoName;
    private String repoFullName;
    private List<String> commitIds;
}
