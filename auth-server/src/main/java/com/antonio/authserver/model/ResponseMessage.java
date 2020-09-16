package com.antonio.authserver.model;

import java.io.Serializable;

public class ResponseMessage implements Serializable {

    private String content;

    public ResponseMessage(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
