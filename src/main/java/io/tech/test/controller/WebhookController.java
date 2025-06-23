package io.tech.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.tech.test.entity.GitCommitEvent;
import io.tech.test.entity.PullRequestDetail;
import io.tech.test.service.PushEventService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")
@Slf4j
public class WebhookController {
	@Autowired
	private PushEventService pushEventService;

	@PostMapping("/receive")
	public ResponseEntity<String> receivePushEvent(@RequestBody Map<String, Object> payload) {
	    log.info("Webhook event received.");
	    log.info("Webhook event received.");
	    log.info("Webhook event received.");

	    if (payload.containsKey("pull_request") && payload.containsKey("action")) {
	        log.info("Processing pull request event.");
	        pushEventService.processPullRequestEvent(payload);
	        return ResponseEntity.ok("Pull request event processed.");
	    }
	    if (Boolean.TRUE.equals(payload.get("created"))) {
	        log.info("Branch creation event detected — ignored.");
	        return ResponseEntity.ok("Branch creation event ignored.");
	    }
	    List<?> commits = (List<?>) payload.get("commits");
	    if (commits == null || commits.isEmpty()) {
	        log.info("Push event with no commits — ignored.");
	        return ResponseEntity.ok("Push event with no commits ignored.");
	    }
	    log.info("Processing push event.");
	    pushEventService.processPushEvent(payload);
	    return ResponseEntity.ok("Push event processed successfully!");
	}

	@GetMapping("/commits/detail")
    public List<GitCommitEvent> getPushEventByBranchAndTicket(@RequestParam(required = false) String branch,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String fieldValueId) {

        List<GitCommitEvent> pushEventOpt = pushEventService.getPushEvent(branch,projectId, fieldValueId);
        return pushEventOpt;
    }
	@GetMapping("/pull-requests")
	public  List<PullRequestDetail>  getPullRequestByBranchAndTicket(
	        @RequestParam String sourceBranch,
	        @RequestParam String ticketId) {

	    List<PullRequestDetail> prDetailOpt = pushEventService.getPullRequest(sourceBranch, ticketId);

	    return prDetailOpt;
	}
}
