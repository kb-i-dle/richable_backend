package com.idle.kb_i_dle_backend.member.service;

import com.idle.kb_i_dle_backend.member.entity.UserApiEntity;
import com.idle.kb_i_dle_backend.member.repository.UserApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;

    @Service
    public class UserApiService {

        @Autowired
        private UserApiRepository userApiRepository;

        public UserApiEntity getUserApiByUid(Integer uid) {
            return userApiRepository.findByUid(uid).orElse(null);  // Optional로 단일 객체 반환하는걸 만듬
        }
    }

