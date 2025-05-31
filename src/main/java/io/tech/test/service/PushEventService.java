package io.tech.test.service;
import io.tech.test.entity.PushEvent;
import io.tech.test.repo.PushEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;

@Service
public class PushEventService {

	@Autowired
	private PushEventRepository pushEventRepository;

    public void processPushEvent(Map<String, Object> payload) {
    	
        PushEvent pushEvent = new PushEvent();

        // Branch and commit info
        String ref = (String) payload.get("ref");
        String branchName = (ref != null) ? ref.replaceFirst("^refs/heads/", "") : null;
        pushEvent.setBranch(branchName);
        pushEvent.setBeforeCommit((String) payload.get("before"));
        pushEvent.setAfterCommit((String) payload.get("after"));

        // Repository block
        Object repoObj = payload.get("repository");
        if (repoObj instanceof Map<?, ?>) {
            Map<String, Object> repository = (Map<String, Object>) repoObj;

            Object repoIdObj = repository.get("id");
            pushEvent.setRepositoryId(repoIdObj != null ? Long.valueOf(repoIdObj.toString()) : null);

            pushEvent.setRepositoryName((String) repository.get("name"));

            Object privateObj = repository.get("private");
            pushEvent.setRepositoryPrivate(privateObj != null ? Boolean.valueOf(privateObj.toString()) : null);

            pushEvent.setRepoUrl((String) repository.get("url"));

            Object ownerObj = repository.get("owner");
            if (ownerObj instanceof Map<?, ?>) {
                Map<String, Object> owner = (Map<String, Object>) ownerObj;
                pushEvent.setRepoOwnerLogin((String) owner.get("login"));
                pushEvent.setRepoOwnerEmail((String) owner.get("email"));
            }
        }

        // Pusher block
        Object pusherObj = payload.get("pusher");
        if (pusherObj instanceof Map<?, ?>) {
            Map<String, Object> pusher = (Map<String, Object>) pusherObj;
            pushEvent.setPusherName((String) pusher.get("name"));
            pushEvent.setPusherEmail((String) pusher.get("email"));
        }

        // Sender block
        Object senderObj = payload.get("sender");
        if (senderObj instanceof Map<?, ?>) {
            Map<String, Object> sender = (Map<String, Object>) senderObj;
            pushEvent.setSenderLogin((String) sender.get("login"));

            Object senderIdObj = sender.get("id");
            pushEvent.setSenderId(senderIdObj != null ? Long.valueOf(senderIdObj.toString()) : null);
        }

        // Other flags
        Object createdObj = payload.get("created");
        pushEvent.setCreated(createdObj != null ? Boolean.valueOf(createdObj.toString()) : null);

        Object deletedObj = payload.get("deleted");
        pushEvent.setDeleted(deletedObj != null ? Boolean.valueOf(deletedObj.toString()) : null);

        Object forcedObj = payload.get("forced");
        pushEvent.setForced(forcedObj != null ? Boolean.valueOf(forcedObj.toString()) : null);

        pushEvent.setCompareUrl((String) payload.get("compare"));

        // Head commit & committer details
        Object headCommitObj = payload.get("head_commit");
        if (headCommitObj instanceof Map<?, ?>) {
            Map<String, Object> headCommit = (Map<String, Object>) headCommitObj;
            String message = (String) headCommit.get("message"); // e.g. "TT#TT-0012 changes,"
            String ticketId = null;
            if (message != null) {
                java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("TT#([A-Z]+-\\d+)").matcher(message);
                if (matcher.find()) {
                    ticketId = matcher.group(1);
                }
            }
            pushEvent.setTicketId(ticketId);
            pushEvent.setHeadCommitMessage((String) headCommit.get("message"));

            Object committerObj = headCommit.get("committer");
            if (committerObj instanceof Map<?, ?>) {
                Map<String, Object> committer = (Map<String, Object>) committerObj;
                pushEvent.setCommitterName((String) committer.get("name"));
                pushEvent.setCommitterEmail((String) committer.get("email"));
                pushEvent.setCommitterUsername((String) committer.get("username"));
            }
       
            String timestampStr = (String) headCommit.get("timestamp");
            if (timestampStr != null) {
                try {
                    pushEvent.setHeadCommitTimestamp(LocalDateTime.parse(timestampStr));
                } catch (Exception e) {
                    pushEvent.setHeadCommitTimestamp(LocalDateTime.now());
                }
            } else {
                pushEvent.setHeadCommitTimestamp(LocalDateTime.now());
            }
        } else {
            pushEvent.setHeadCommitTimestamp(LocalDateTime.now());
        }

        // Save entity
        pushEventRepository.save(pushEvent);
    }

}