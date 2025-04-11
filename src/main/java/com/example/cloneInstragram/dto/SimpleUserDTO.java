package com.example.cloneInstragram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleUserDTO {
    private Long id;

    @JsonProperty("username")
    private String username;

    public SimpleUserDTO() {}

    public SimpleUserDTO(String username) {
        this.username = username;
    }

    // Переопределяем toString, чтобы избежать лишних данных
    @Override
    public String toString() {
        return "SimpleUserDTO{username='" + username + "'}";
    }
}