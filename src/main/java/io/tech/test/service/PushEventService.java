package io.tech.test.service;

import io.tech.test.entity.GitCommitEvent;
import io.tech.test.repo.PushEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PushEventService {

    @Autowired
    private PushEventRepository pushEventRepository;

    public void processPushEvent(Map<String, Object> payload) {
        GitCommitEvent gitCommitEvent = new GitCommitEvent();

        try {
            // Extract branch info
            String ref = (String) payload.get("ref");
            String branchName = (ref != null) ? ref.replaceFirst("^refs/heads/", "") : null;
            gitCommitEvent.setBranch(branchName);
            gitCommitEvent.setCommitId((String) payload.get("after"));
            log.info("📌 Received push event on branch: {}", branchName);
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            if (repository != null) {
                gitCommitEvent.setRepoName((String) repository.get("name"));
                gitCommitEvent.setRepoFullName((String) repository.get("full_name"));
                gitCommitEvent.setRepoUrl((String) repository.get("url"));
            }
            Map<String, Object> pusher = (Map<String, Object>) payload.get("pusher");
            if (pusher != null) {
                gitCommitEvent.setPusherName((String) pusher.get("name"));
                gitCommitEvent.setPusherEmail((String) pusher.get("email"));
                log.info("🧑‍💻 Pusher: {} <{}>", pusher.get("name"), pusher.get("email"));
            }
            Map<String, Object> headCommit = (Map<String, Object>) payload.get("head_commit");
            if (headCommit != null) {
            	String message = (String) headCommit.get("message");
            	gitCommitEvent.setMessage(message);
                // Extract ticket ID from commit message
                String ticketId = null;
                if (message != null) {
                    Matcher matcher = Pattern.compile("TT#([A-Z]+-\\d+)").matcher(message);
                    if (matcher.find()) {
                        ticketId = matcher.group(1);
                    }
                }
                gitCommitEvent.setTicketId(ticketId);
                Map<String, Object> committer = (Map<String, Object>) headCommit.get("committer");
                if (committer != null) {
                    String authorName = (String) committer.get("name");
                    String authorEmail = (String) committer.get("email");
                    String username = (String) committer.get("username");

                    gitCommitEvent.setAuthorName(authorName);
                    gitCommitEvent.setAuthorEmail(authorEmail);
                    gitCommitEvent.setUsername(username);
                    boolean committedByBot = (authorName != null && authorName.contains("[bot]"))
                            || (username != null && username.contains("[bot]"))
                            || (authorEmail != null && authorEmail.contains("noreply.github.com"));

                    gitCommitEvent.setCommittedByBot(committedByBot);
                    log.info("🤖 Commit by bot? {}", committedByBot);
                }
                String timestampStr = (String) headCommit.get("timestamp");
                if (timestampStr != null) {
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse(timestampStr);
                        gitCommitEvent.setUpdatedDate(odt.toLocalDateTime());
                    } catch (Exception e) {
                        log.warn("Failed to parse commit timestamp: {}", timestampStr);
                        gitCommitEvent.setUpdatedDate(LocalDateTime.now());
                    }
                } else {
                    gitCommitEvent.setUpdatedDate(LocalDateTime.now());
                }
                String commitUrl = (String) headCommit.get("url");
                if (commitUrl != null) {
                    gitCommitEvent.setUrl(commitUrl);
                }
            }
            gitCommitEvent.setCommitCount(1);
            pushEventRepository.save(gitCommitEvent);
            log.info("✅ Push event saved for branch '{}' with ticket '{}'", branchName, gitCommitEvent.getTicketId());

        } catch (Exception e) {
            log.error("❌ Error processing push event: {}", e.getMessage(), e);
        }
    }

    public Optional<GitCommitEvent> getPushEvent(String branch, String ticketId) {
        // To be implemented
        return Optional.empty();
    }
}
