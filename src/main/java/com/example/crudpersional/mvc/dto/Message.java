package com.example.crudpersional.mvc.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Message {
    String message = "";
    String href = "";

    public Message(String message, String href) {
        this.message = message;
        this.href = href;
    }
}