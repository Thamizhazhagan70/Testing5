package io.tech.test.service;

import io.tech.test.entity.PushEvent;
import io.tech.test.repo.PushEventRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PushEventService {

	@Autowired
	private PushEventRepository pushEventRepository;

	public void processPushEvent(Map<String, Object> payload) {
		PushEvent pushEvent = new PushEvent();
		try {
			// Extract branch info
			String ref = (String) payload.get("ref");
			String branchName = (ref != null) ? ref.replaceFirst("^refs/heads/", "") : null;
			pushEvent.setBranch(branchName);
			pushEvent.setBeforeCommit((String) payload.get("before"));
			pushEvent.setAfterCommit((String) payload.get("after"));
			log.info("Received push event on branch: {}", branchName);

			if (branchName != null) {
				Optional<PushEvent> existingPushEventOpt = pushEventRepository.findByBranch(branchName);
				if (existingPushEventOpt.isPresent()) {
					PushEvent existingPushEvent = existingPushEventOpt.get();
					existingPushEvent.setCommitCount(existingPushEvent.getCommitCount() + 1);

					Object headCommitObj = payload.get("head_commit");
					if (headCommitObj instanceof Map<?, ?> headCommit) {
						String message = (String) headCommit.get("message");
						if (message != null) {
							if (existingPushEvent.getHeadCommitMessages() == null) {
								existingPushEvent.setHeadCommitMessages(new ArrayList<>());
							}
							existingPushEvent.getHeadCommitMessages().add(message);
							log.info("📥 Appended new commit message: {}", message);
						}
						String timestampStr = (String) headCommit.get("timestamp");
						try {
							existingPushEvent.setHeadCommitTimestamp(
									timestampStr != null ? LocalDateTime.parse(timestampStr) : LocalDateTime.now());
						} catch (Exception e) {
							log.warn("Failed to parse head commit timestamp: {}", timestampStr);
							existingPushEvent.setHeadCommitTimestamp(LocalDateTime.now());
						}
					}
					existingPushEvent.setCompareUrl((String) payload.get("compare"));

					pushEventRepository.save(existingPushEvent);
					log.info("Updated commit count to {}, and appended message for branch '{}'",
							existingPushEvent.getCommitCount(), branchName);
					return;
				}
			}
			// Extract repository info
			Object repoObj = payload.get("repository");
			if (repoObj instanceof Map<?, ?> repository) {
				Object repoIdObj = repository.get("id");
				if (repoIdObj != null) {
					try {
						pushEvent.setRepositoryId(Long.valueOf(repoIdObj.toString()));
					} catch (NumberFormatException e) {
						log.warn("Invalid repository ID format: {}", repoIdObj);
					}
				}
				pushEvent.setRepositoryName((String) repository.get("name"));
				Object privateObj = repository.get("private");
				if (privateObj != null) {
					pushEvent.setRepositoryPrivate(Boolean.valueOf(privateObj.toString()));
				}
				pushEvent.setRepoUrl((String) repository.get("url"));
				Object ownerObj = repository.get("owner");
				if (ownerObj instanceof Map<?, ?> owner) {
					pushEvent.setRepoOwnerLogin((String) owner.get("login"));
					pushEvent.setRepoOwnerEmail((String) owner.get("email"));
				}
			}
			// Extract pusher info
			Object pusherObj = payload.get("pusher");
			if (pusherObj instanceof Map<?, ?> pusher) {
				pushEvent.setPusherName((String) pusher.get("name"));
				pushEvent.setPusherEmail((String) pusher.get("email"));
				log.info("🧑‍💻 Pusher: {} <{}>", pusher.get("name"), pusher.get("email"));
			}
			// Extract sender info
			Object senderObj = payload.get("sender");
			if (senderObj instanceof Map<?, ?> sender) {
				pushEvent.setSenderLogin((String) sender.get("login"));

				Object senderIdObj = sender.get("id");
				if (senderIdObj != null) {
					try {
						pushEvent.setSenderId(Long.valueOf(senderIdObj.toString()));
					} catch (NumberFormatException e) {
						log.warn("⚠️ Invalid sender ID format: {}", senderIdObj);
					}
				}
			}

			// Flags: created, deleted, forced
			Object createdObj = payload.get("created");
			if (createdObj != null)
				pushEvent.setCreated(Boolean.valueOf(createdObj.toString()));

			Object deletedObj = payload.get("deleted");
			if (deletedObj != null)
				pushEvent.setDeleted(Boolean.valueOf(deletedObj.toString()));

			Object forcedObj = payload.get("forced");
			if (forcedObj != null)
				pushEvent.setForced(Boolean.valueOf(forcedObj.toString()));

			pushEvent.setCompareUrl((String) payload.get("compare"));

			// Extract head commit info
			Object headCommitObj = payload.get("head_commit");
			if (headCommitObj instanceof Map<?, ?> headCommit) {
				String message = (String) headCommit.get("message");
				pushEvent.setHeadCommitMessages(Collections.singletonList(message));

				// Extract ticket ID from message
				String ticketId = null;
				if (message != null) {
					java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("TT#([A-Z]+-\\d+)")
							.matcher(message);
					if (matcher.find()) {
						ticketId = matcher.group(1);
					}
				}
				pushEvent.setTicketId(ticketId);
				log.info("🎫 Commit message: {}", message);
				log.info("🎯 Extracted ticket ID: {}", ticketId);

				Object committerObj = headCommit.get("committer");
				if (committerObj instanceof Map<?, ?> committer) {
					pushEvent.setCommitterName((String) committer.get("name"));
					pushEvent.setCommitterEmail((String) committer.get("email"));
					pushEvent.setCommitterUsername((String) committer.get("username"));
				}

				String timestampStr = (String) headCommit.get("timestamp");
				try {
				    if (timestampStr != null) {
				        OffsetDateTime odt = OffsetDateTime.parse(timestampStr);
				        pushEvent.setHeadCommitTimestamp(odt.toLocalDateTime());
				    } else {
				        pushEvent.setHeadCommitTimestamp(LocalDateTime.now());
				    }
				} catch (Exception e) {
				    log.warn("Failed to parse head commit timestamp: {}", timestampStr);
				    pushEvent.setHeadCommitTimestamp(LocalDateTime.now());
				}

			} else {
				pushEvent.setHeadCommitTimestamp(LocalDateTime.now());
			}

			// Set initial commit count for new push event
			pushEvent.setCommitCount(1);

			// Save new push event
			pushEventRepository.save(pushEvent);
			log.info("✅ Push event saved for branch '{}' with ticket '{}'", branchName, pushEvent.getTicketId());

		} catch (Exception e) {
			log.error("❌ Error processing push event: {}", e.getMessage(), e);
		}
	}

}