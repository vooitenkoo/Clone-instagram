package com.example.cloneInstragram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleUserDTO {
    private Long id;

    @JsonProperty("username")
    private String username;

    public SimpleUserDTO() {}

    public SimpleUserDTO(String username) {
        this.username = username;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Переопределяем toString, чтобы избежать лишних данных
    @Override
    public String toString() {
        return "SimpleUserDTO{username='" + username + "'}";
    }
}