package com.idle.kb_i_dle_backend.domain.member.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);
}
