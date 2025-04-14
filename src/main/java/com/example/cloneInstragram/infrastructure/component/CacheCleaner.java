package com.example.cloneInstragram.infrastructure.component;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class CacheCleaner {

    @CacheEvict(value = {"users", "usersById", "userChats", "chatMessagesList"}, allEntries = true)
    public void clearAllCaches() {
        System.out.println("Clearing all caches...");
    }
}