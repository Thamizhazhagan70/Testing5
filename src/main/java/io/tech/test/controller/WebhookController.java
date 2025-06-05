package io.tech.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.tech.test.entity.GitCommitEvent;
import io.tech.test.service.PushEventService;
import lombok.extern.slf4j.Slf4j;

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
			
			log.info("pull Request uhhhhhhhhhhhhhh.");
		} else if (!payload.containsKey("head_commit") && !payload.containsKey("commits")) {
			log.info("not save crate api");

		} else if (payload.containsKey("head_commit")) {
			log.info("start commit save");
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
