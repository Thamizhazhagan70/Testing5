package io.tech.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.tech.test.entity.GitCommitEvent;
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
		log.info("✅ Push event received!");
		log.info("Raw Payload: {}", payload);
		if (payload.containsKey("pull_request") && payload.containsKey("action")) {
			log.info("Pull request event detected.");
			pushEventService.processPullRequestEvent(payload);
			return new ResponseEntity<>("Pull request event processed.", HttpStatus.OK);
		} else if (payload.containsKey("created") && (Boolean) payload.get("created")) {
			log.info("Branch creation event detected. Not processing this payload.");
			return new ResponseEntity<>("Branch creation event ignored.", HttpStatus.OK);
		} else if (!payload.containsKey("commits") || ((List<?>) payload.get("commits")).isEmpty()) {
			log.info("Push event with no commits detected. Not processing this payload.");
			return new ResponseEntity<>("Push event with no commits ignored.", HttpStatus.OK);
		} else {
			log.info("Processing push event.");
			pushEventService.processPushEvent(payload);
		}
		return new ResponseEntity<>("Push event processed successfully!", HttpStatus.OK);
	}

//    @GetMapping("/push-event")
//    public PushEvent getPushEventByBranchAndTicket(
//            @RequestParam String branch,
//            @RequestParam String ticketId) {
//
//        Optional<PushEvent> pushEventOpt = pushEventService.getPushEvent(branch, ticketId);
//        return pushEventOpt.orElse(null);  // returns null if not found
//    }

}
