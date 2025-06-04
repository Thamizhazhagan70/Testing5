package io.tech.test.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitsDetailsDTO {

    private String ref;
    private String ref_type;
    private String before;
    private String after;
    private RepositoryDto repository;
    private PusherDto pusher;
    private SenderDto sender;
    private boolean created;
    private boolean deleted;
    private boolean forced;
    private String base_ref;
    private String compare;
    private List<CommitDto> commits;
    private CommitDto headCommit;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RepositoryDto {
        private long id;
        private String name;
        private String fullName;
        private OwnerDto owner;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OwnerDto {
        private String name;
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PusherDto {
        private String name;
        private String email;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SenderDto {
        private String login;
        private long id;
        private String htmlUrl;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommitDto {
        private String id;
        private String message;
        private String timestamp;
        private String url;
        private AuthorDto author;
        private AuthorDto committer;
        private List<String> added;
        private List<String> removed;
        private List<String> modified;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorDto {
        private String name;
        private String email;
        private String username;
    }
}
