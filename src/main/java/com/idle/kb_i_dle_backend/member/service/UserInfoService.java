package com.idle.kb_i_dle_backend.member.service;

import com.idle.kb_i_dle_backend.member.dto.UserApiDTO;
import com.idle.kb_i_dle_backend.member.dto.UserInfoDTO;
import com.idle.kb_i_dle_backend.member.entity.User;
import com.idle.kb_i_dle_backend.member.entity.UserApiEntity;
import com.idle.kb_i_dle_backend.member.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
//
@Service
public class UserInfoService {

    @Autowired
    private UserRepository userRepository;

    // 기존 사용자의 정보를 가져온다
    public UserInfoDTO getUserInfoByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname);

        if (user != null) {
            // UserApiEntity -> UserApiDTO 변환
            UserApiDTO userApiDTO = null;
            if (user.getUserApi() != null) {
                UserApiEntity userApiEntity = user.getUserApi();
                userApiDTO = new UserApiDTO(
                        userApiEntity.getUid(),
                        userApiEntity.getApi_bank(),
                        userApiEntity.getApi_stock(),
                        userApiEntity.getApi_stock_token(),
                        userApiEntity.getApi_stock_secret(),
                        userApiEntity.getApi_stock_app(),
                        userApiEntity.getApi_coin(),
                        userApiEntity.getApi_coin_app(),
                        userApiEntity.getApi_coin_secret()
                );
            }

            // User -> UserInfoDTO 변환
            return new UserInfoDTO(
                    user.getUid(),
                    user.getId(),
                    user.getNickname(),
                    user.getAuth(),
                    user.getEmail(),
                    user.getBirth_year(),
                    user.getGender(),
                    Boolean.TRUE.equals(user.getIs_certification()),
                    user.getProfile(),  // 'profile' 필드를 'img'로 매핑
                    userApiDTO  // 변환된 UserApiDTO 포함
            );
        }

        return null;  // 사용자 정보를 찾지 못한 경우 null 반환
    }

    // 사용자 정보를 업데이트하는 메서드 추가
    public UserInfoDTO updateUserInfo(UserInfoDTO updatedUserInfo) throws Exception {
        // 닉네임을 사용하여 사용자 찾기
        User user = userRepository.findByNickname(updatedUserInfo.getNickname());

        if (user == null) {
            throw new Exception("사용자를 찾을 수 없습니다.");
        }

        // UserInfoDTO의 정보를 User 엔티티에 반영
        user.setEmail(updatedUserInfo.getEmail());  // setEmail()으로 수정
        user.setProfile(updatedUserInfo.getImg());  // 'img' 필드를 'profile'로 매핑
        user.setBirth_year(updatedUserInfo.getBirthYear());
        user.setGender(updatedUserInfo.getGender());

        // 사용자 정보 저장
        userRepository.save(user);

        // 업데이트된 사용자 정보를 반환 (UserInfoDTO 변환)
        return new UserInfoDTO(
                user.getUid(),
                user.getId(),
                user.getNickname(),
                user.getAuth(),
                user.getEmail(),
                user.getBirth_year(),
                user.getGender(),
                Boolean.TRUE.equals(user.getIs_certification()),
                user.getProfile(),  // 'profile' 필드를 'img'로 매핑
                null  // API 정보는 필요 없으므로 null
        );
    }
}
