package com.library.model;

public class EmailMessage {

    private final String to;
    private final String content;

    public EmailMessage(String to, String content) {
        this.to = to;
        this.content = content;
    }

    public String getTo() {
        return to;
    }

    public String getContent() {
        return content;
    }
}
