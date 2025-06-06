package io.tech.test.service;

import io.tech.test.entity.GitCommitEvent;
import io.tech.test.entity.PullRequestDetail;
import io.tech.test.repo.PullRequestDetailRepository;
import io.tech.test.repo.PushEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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

	@Autowired
	private PullRequestDetailRepository pullRequestDetailRepository;

	public void processPushEvent(Map<String, Object> payload) {
		GitCommitEvent gitCommitEvent = new GitCommitEvent();

		try {
			String ref = (String) payload.get("ref");
			String branchName = ref != null ? ref.replaceFirst("^refs/heads/", "") : null;
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
			}

			Map<String, Object> headCommit = (Map<String, Object>) payload.get("head_commit");
			if (headCommit != null) {
				String message = (String) headCommit.get("message");
				log.info(message);
				gitCommitEvent.setMessage(message);

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
					gitCommitEvent.setCommitUrl(commitUrl);
				}
			}

			pushEventRepository.save(gitCommitEvent);
			log.info("✅ Push event saved for branch '{}' with ticket '{}'", branchName, gitCommitEvent.getTicketId());

		} catch (Exception e) {
			log.error("❌ Error processing push event: {}", e.getMessage(), e);
		}
	}

	public void processPullRequestEvent(Map<String, Object> payload) {
		Map<String, Object> pullRequest = (Map<String, Object>) payload.get("pull_request");
		if (pullRequest == null)
			return;

		String prId = pullRequest.get("id") != null ? pullRequest.get("id").toString() : null;
		if (prId == null)
			return;

		PullRequestDetail prDetail = pullRequestDetailRepository.findByPullRequestId(prId)
				.orElse(new PullRequestDetail());

		prDetail.setPullRequestId(prId);
		String message = (String) pullRequest.get("title");
		String ticketId = null;
		if (message != null) {
			Matcher Ticket = Pattern.compile("TT#([A-Z]+-\\d+)").matcher(message);
			if (Ticket.find()) {
				ticketId = Ticket.group(1);
			}
			prDetail.setTicketId(ticketId);
			String cleanMessage = message.replaceFirst("^TT#[^:]+:\\s*", "");
			prDetail.setMessage(cleanMessage);
			Matcher matcher = Pattern.compile("TT#([A-Z]+-\\d+)").matcher(message);
			if (matcher.find()) {
				ticketId = matcher.group(1);
			}
		}
		prDetail.setPullRequestUrl((String) pullRequest.get("html_url"));
		prDetail.setStatus((String) pullRequest.get("state"));
		if (prDetail.getCreatedDate() == null) {
			prDetail.setCreatedDate((String) pullRequest.get("created_at"));
		}
		prDetail.setMergedDate((String) pullRequest.get("merged_at"));
		prDetail.setIsMerged((Boolean) pullRequest.get("merged"));

		Map<String, Object> head = (Map<String, Object>) pullRequest.get("head");
		String sourceBranch = null;
		if (head != null) {
			sourceBranch = (String) head.get("ref");
			prDetail.setSourceBranch(sourceBranch);
			Map<String, Object> headRepo = (Map<String, Object>) head.get("repo");
			if (headRepo != null) {
				prDetail.setRepoName((String) headRepo.get("name"));
				prDetail.setRepoFullName((String) headRepo.get("full_name"));
			}
		}
		Map<String, Object> requestedReviewer = (Map<String, Object>) pullRequest.get("requested_reviewer");
		if (requestedReviewer != null) {
			prDetail.setRequestedReviewer((String) requestedReviewer.get("login"));
		}
		Map<String, Object> mergedBy = (Map<String, Object>) pullRequest.get("merged_by");
		if (mergedBy != null) {
			prDetail.setMerged_By((String) requestedReviewer.get("login"));
		}

		Map<String, Object> base = (Map<String, Object>) pullRequest.get("base");
		if (base != null) {
			prDetail.setTargetBranch((String) base.get("ref"));
		}
		Map<String, Object> user = (Map<String, Object>) pullRequest.get("user");
		if (user != null) {
			prDetail.setCreatedBy((String) user.get("login"));
		}
		pullRequestDetailRepository.save(prDetail);
	}

	public Optional<GitCommitEvent> getPushEvent(String branch, String ticketId) {
		return Optional.empty();
	}
}
