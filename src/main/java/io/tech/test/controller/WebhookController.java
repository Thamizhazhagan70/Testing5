package io.tech.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.tech.test.entity.PushEvent;
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

		System.out.println("Ref: " + payload.get("ref"));
		if (payload.containsKey("action") && payload.get("action").equals("review_requested")) {
			log.info("<<<<<<<payload.containsKey(\"action\") && payload.get(\"action\").equals(\"review_requested\")>>>>>>>>");
			
		}
		else if(!payload.containsKey("action") && !payload.get("action").equals("review_requested")) {
			log.info("<<<<<<<!!!payload.containsKey(\\\"action\\\") && !!payload.get(\\\"action\\\").equals(\\\"review_requested\\\")>>>>>>>>");
		}
		else {
			
			log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		
		
//		pushEventService.processPushEvent(payload);
		log.info("✅ Push event closed!");
		return new ResponseEntity<>("Push event processed successfully!", HttpStatus.OK);
	}
	
	@GetMapping("/commits/details")
	public PushEvent getCommitDetails(@RequestParam(required = false) String ticketId,
	                                          @RequestParam(required = false) String projectId,
	                                          @RequestParam(required = false) String fieldValueId){

        Optional<PushEvent> pushEventOpt = pushEventService.getPushEvent(ticketId, projectId,fieldValueId);
        return pushEventOpt.orElse(null);  // returns null if not found
    }
	
}
