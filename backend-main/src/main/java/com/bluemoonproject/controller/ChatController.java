package com.bluemoonproject.controller;

import com.bluemoonproject.dto.request.ChatRequestDto;
import com.bluemoonproject.dto.response.ChatResponse;
import com.bluemoonproject.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*") // Allow frontend to access
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequestDto request) {
        return ResponseEntity.ok(chatService.processMessage(request));
    }

    @GetMapping
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("Chatbot service is running!");
    }
}
