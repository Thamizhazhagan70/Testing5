package io.tech.test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @PostMapping("/receive")
    public ResponseEntity<String> receivePushEvent(@RequestBody Map<String, Object> payload) {
        System.out.println("✅ Push event received!");

        // Print entire payload
        System.out.println(payload);

        // Print specific details
        System.out.println("Ref: " + payload.get("ref"));

        Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
        if (repository != null) {
            System.out.println("Repository Name: " + repository.get("name"));
            System.out.println("Repository Full Name: " + repository.get("full_name"));
        }

        Map<String, Object> pusher = (Map<String, Object>) payload.get("pusher");
        if (pusher != null) {
            System.out.println("Pusher Name: " + pusher.get("name"));
            System.out.println("Pusher Email: " + pusher.get("email"));
        }

        // You can also access commits list if needed
        // List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");

        return new ResponseEntity<>("Push event processed successfully!", HttpStatus.OK);
    }
}
