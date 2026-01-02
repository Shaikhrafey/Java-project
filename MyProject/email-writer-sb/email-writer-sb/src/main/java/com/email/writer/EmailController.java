package com.email.writer;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class EmailController {
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailrequest)  {
        String response=emailGeneratorService.generateEmailReply(emailrequest);
        return ResponseEntity.ok(response);
    }

}
