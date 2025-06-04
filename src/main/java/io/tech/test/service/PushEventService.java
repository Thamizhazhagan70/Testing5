package io.tech.test.service;

import io.tech.test.dto.CommitsDetailsDTO;
import io.tech.test.dto.PushEventDTO;
import io.tech.test.entity.PushEvent;
import io.tech.test.repo.PushEventRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	public void processPushEvent(CommitsDetailsDTO commitsDetailsDTO) {
	    PushEvent pushEvent = new PushEvent();
	    try {
	        // Extract branch info
	        String ref = commitsDetailsDTO.getRef();
	        String branchName = (ref != null) ? ref.replaceFirst("^refs/heads/", "") : null;
	        pushEvent.setBranch(branchName);

	        if (commitsDetailsDTO.getCommits() != null && !commitsDetailsDTO.getCommits().isEmpty()) {
	            String commitId = commitsDetailsDTO.getCommits().get(0).getId();
	            pushEvent.setCommitId(commitId);
	        }

	        log.info("📌 Received push event on branch: {}", branchName);

	        // Check for existing push event for this branch
	        if (branchName != null) {
	            Optional<PushEvent> existingPushEventOpt = pushEventRepository.findByBranch(branchName);

	            if (existingPushEventOpt.isPresent() && existingPushEventOpt.get().getId() != null) {
	                PushEvent existingPushEvent = existingPushEventOpt.get();

	                existingPushEvent.setCommitCount(existingPushEvent.getCommitCount() + 1);

	                CommitsDetailsDTO.CommitDto headCommit = commitsDetailsDTO.getHeadCommit();
	                if (headCommit != null) {
	                    String message = headCommit.getMessage();

	                    if (message != null) {
	                        if (existingPushEvent.getMessages() == null) {
	                            existingPushEvent.setMessages(new ArrayList<>());
	                        }
	                        existingPushEvent.getMessages().add(message);
	                        log.info("📥 Appended new commit message: {}", message);

	                        // Extract ticket id
	                        String ticketId = null;
	                        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("TT#([A-Z]+-\\d+)").matcher(message);
	                        if (matcher.find()) {
	                            ticketId = matcher.group(1);
	                        }
	                        existingPushEvent.setTicketId(ticketId);
	                    }

	                    String timestampStr = headCommit.getTimestamp();
	                    try {
	                        if (timestampStr != null) {
	                            OffsetDateTime odt = OffsetDateTime.parse(timestampStr);
	                            existingPushEvent.setHeadCommitTimestamp(odt.toLocalDateTime());
	                        } else {
	                            existingPushEvent.setHeadCommitTimestamp(LocalDateTime.now());
	                        }
	                    } catch (Exception e) {
	                        log.warn("Failed to parse head commit timestamp: {}", timestampStr);
	                        existingPushEvent.setHeadCommitTimestamp(LocalDateTime.now());
	                    }
	                }

	                existingPushEvent.setCommitUrl(commitsDetailsDTO.getCompare());
	                log.info("Compare URL: {}", commitsDetailsDTO.getCompare());

	                pushEventRepository.save(existingPushEvent);
	                log.info("✅ Updated existing push event for branch '{}'", branchName);
	                return;
	            }
	        }

	        // Repository info
	        CommitsDetailsDTO.RepositoryDto repo = commitsDetailsDTO.getRepository();
	        if (repo != null) {
	            pushEvent.setRepositoryId(repo.getId());
	            pushEvent.setRepositoryName(repo.getName());
	            pushEvent.setRepoUrl(repo.getFullName());
	            CommitsDetailsDTO.OwnerDto owner = repo.getOwner();
	            if (owner != null) {
	                pushEvent.setRepoOwnerLogin(owner.getName());
	                pushEvent.setRepoOwnerEmail(owner.getEmail());
	            }
	        }

	        // Pusher info
	        CommitsDetailsDTO.PusherDto pusher = commitsDetailsDTO.getPusher();
	        if (pusher != null) {
	            pushEvent.setPusherName(pusher.getName());
	            pushEvent.setPusherEmail(pusher.getEmail());
	        }

	        // Sender info
	        CommitsDetailsDTO.SenderDto sender = commitsDetailsDTO.getSender();
	        if (sender != null) {
	            pushEvent.setSenderLogin(sender.getLogin());
	            pushEvent.setSenderId(sender.getId());
	        }

	        // Flags
	        pushEvent.setCreated(commitsDetailsDTO.isCreated());
	        pushEvent.setDeleted(commitsDetailsDTO.isDeleted());
	        pushEvent.setForced(commitsDetailsDTO.isForced());

	        pushEvent.setCommitUrl(commitsDetailsDTO.getCompare());
	        log.info("Compare URL: {}", commitsDetailsDTO.getCompare());

	        // Head Commit info
	        CommitsDetailsDTO.CommitDto headCommit = commitsDetailsDTO.getHeadCommit();
	        if (headCommit != null) {
	            String message = headCommit.getMessage();
	            pushEvent.setMessages(message != null ? Collections.singletonList(message) : Collections.emptyList());

	            String ticketId = null;
	            if (message != null) {
	                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("TT#([A-Z]+-\\d+)").matcher(message);
	                if (matcher.find()) {
	                    ticketId = matcher.group(1);
	                }
	            }
	            pushEvent.setTicketId(ticketId);

	            log.info("🎫 Commit message: {}", message);
	            log.info("🎯 Extracted ticket ID: {}", ticketId);

	            CommitsDetailsDTO.AuthorDto committer = headCommit.getCommitter();
	            if (committer != null) {
	                pushEvent.setCommitterName(committer.getName());
	                pushEvent.setCommitterEmail(committer.getEmail());
	                pushEvent.setCommitterUsername(committer.getUsername());
	            }

	            String timestampStr = headCommit.getTimestamp();
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

	        pushEvent.setCommitCount(1);

	        pushEventRepository.save(pushEvent);
	        log.info("✅ Push event saved for branch '{}' with ticket '{}'", branchName, pushEvent.getTicketId());

	    } catch (Exception e) {
	        log.error("❌ Error processing push event: {}", e.getMessage(), e);
	    }
	}


//	public void processPayload(Map<String, Object> payload) {
//		if (payload.containsKey("action") && "review_requested".equals(payload.get("action"))) {
//			log.info("🔍 Pull Request - Review Requested event detected");
//			Map<String, Object> prNode = (Map<String, Object>) payload.get("pull_request");
//			if (prNode != null) {
//				String htmlUrl = (String) prNode.get("html_url");
//				log.info("📎 PR URL: {}", htmlUrl);
//				// Additional processing for PR can be added here
//			}
//		}
//		// Check for Push Event only if it's not a PR event
//		else if (!payload.containsKey("action") && payload.containsKey("ref") && payload.containsKey("repository")) {
//			log.info("📤 Push Event detected");
//			String branch = (String) payload.get("ref");
//			
//
//			// Create an instance of ObjectMapper
//			ObjectMapper objectMapper = new ObjectMapper();
//			
//			PushEventDTO commitDetails = objectMapper.convertValue(payload, PushEventDTO.class);
//
//			// Create a new PushEventDTO object
//			PushEventDTO pushEventDTO = new PushEventDTO();
//			// Extract values from the payload
//			pushEventDTO.setBranch(branch);
//			String committUrl = (String) payload.get("compare");
//			pushEventDTO.setCommitUrl(committUrl);
//			// Set other fields as necessary
//			Map<String, Object> repoNode = (Map<String, Object>) payload.get("repository");
//			if (repoNode != null) {
//				String repoName = (String) repoNode.get("name");
//				pushEventDTO.setRepoName(repoName);
//			}
//			// Assuming you have a way to get commit details from the payload
//			List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");
//			if (commits != null && !commits.isEmpty()) {
//				Map<String, Object> latestCommit = commits.get(0); // Get the latest commit
//				pushEventDTO.setCommitId((String) latestCommit.get("id"));
//				pushEventDTO.setCommitUrl((String) latestCommit.get("url"));
//				pushEventDTO.setMessage((String) latestCommit.get("message"));
//				pushEventDTO.setCommitDate((String) latestCommit.get("timestamp"));
//			}
////			
//			if (repoNode != null) {
//				String repoName = (String) repoNode.get("name");
////				processPushEvent(pushEventDTO);
//			}
//		}
//		// else Handle unknown or unhandled payload types
//		else {
//			log.warn("⚠️ Unknown or unhandled payload type");
//		}
//
//		
//	}
//	
//	public Optional<PushEvent> getPushEvent(String branch, String ticketId, String fieldValueId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//

}