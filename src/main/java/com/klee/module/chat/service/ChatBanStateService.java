package com.klee.module.chat.service;

public interface ChatBanStateService {

    boolean isUserBanned(Long userId);

    String getBanReason(Long userId);

    void banUser(Long userId, String reason);

    void unbanUser(Long userId);
}