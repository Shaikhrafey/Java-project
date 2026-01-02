package com.email.writer;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private  String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder().build();
    }

//
//    public EmailGeneratorService(WebClient.Builder webClientBuilder, @Value("${gemini.api.url}") String baseUrl,
//                                 @Value("${gemini.api.key}")String geminiApiKey) {
//        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
//        this.apiKey=geminiApiKey;
//    }


    public String generateEmailReply(EmailRequest emailRequest) {
        String prompt=buildPrompt(emailRequest);
        Map<String, Object> requestBody=Map.of(
                "contents",new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)


                        })
                }
        );

        String response=webClient.post().uri(geminiApiUrl+geminiApiKey).
                header("Content-Type","application/json").bodyValue(requestBody)
                .retrieve().bodyToMono(String.class).block();
       return extractResponseContent(response);}

    private String extractResponseContent(String response){

        try {
            ObjectMapper mapper= new ObjectMapper();
            JsonNode root= mapper.readTree(response);
            return root.path("candidates").get(0).path("content").path("parts").get(0).
                    path("text").asText();



        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }



    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content.Start email with By Farhan khan  ");
        if(emailRequest.getTone()!=null&&!emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
            
        }
        prompt.append("\nOriginal Email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }

}
