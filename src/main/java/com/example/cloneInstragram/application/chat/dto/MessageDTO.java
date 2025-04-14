package com.example.cloneInstragram.application.chat.dto;

import com.example.cloneInstragram.application.user.dto.SimpleUserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MessageDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("sender")
    private SimpleUserDTO sender;

    @JsonProperty("content")
    private String content;

    @JsonProperty("chatId")
    private Long chatId;

    public MessageDTO(Long id, SimpleUserDTO sender, String content, LocalDateTime sentAt, boolean read) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
        this.read = read;
    }

    @JsonProperty("read")
    private boolean read;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime sentAt;


    public MessageDTO() {}

    public MessageDTO(Long id, SimpleUserDTO sender, String content, LocalDateTime sentAt) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "MessageDTO{id=" + id + ", sender=" + sender + ", content='" + content + "', chatId=" + chatId + ", sentAt=" + sentAt + "}";
    }

}
