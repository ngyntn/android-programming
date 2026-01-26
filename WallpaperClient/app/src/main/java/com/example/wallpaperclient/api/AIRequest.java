package com.example.wallpaperclient.api;

public class AIRequest {
    private String prompt;

    public AIRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
