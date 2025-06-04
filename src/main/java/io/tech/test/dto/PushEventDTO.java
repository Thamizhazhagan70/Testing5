package io.tech.test.dto;
import lombok.Data;

@Data
public class PushEventDTO {
    private String branch;
    private String commitId;
    private String commitUrl;
    private String message;
    private String fieldValueId;
    private String projectId;
    private String pullRequestUrl;
    private String pullRequestId;
    private String repoName;
    private String username;
    private String commitDate;
}
