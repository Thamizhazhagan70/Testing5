package io.tech.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.tech.test.service.PushEventService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

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
		pushEventService.processPushEvent(payload);
		log.info("✅ Push event closed!");
		return new ResponseEntity<>("Push event processed successfully!", HttpStatus.OK);
	}
	
	
}
