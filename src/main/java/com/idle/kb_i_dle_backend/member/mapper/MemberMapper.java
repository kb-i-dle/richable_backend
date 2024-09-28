package com.idle.kb_i_dle_backend.member.mapper;

import com.idle.kb_i_dle_backend.member.dto.MemberDTO;
import com.idle.kb_i_dle_backend.member.dto.MemberJoinDTO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MemberMapper {

    @Select("SELECT COUNT(*) > 0 FROM user_info.user_info WHERE id = #{id}")
    boolean checkDupl(String id);

    @Select("SELECT * FROM user_info.user_info WHERE id = #{id}")
    MemberDTO getMember(String id);

    @Select("SELECT id, nickname, gender, email, birth_year FROM user_info.user_info WHERE id = #{id}")
    MemberJoinDTO getMemberJoin(String id);

    @Select("SELECT * FROM user_info.user_info WHERE id = #{id}")
    MemberDTO findById(String id);

    @Insert("INSERT INTO user_info.user_info (id, password, nickname, gender, email, birth_year) " +
            "VALUES (#{id}, #{password}, #{nickname}, #{gender}, #{email}, #{birth_year})")
    void insertNewMember(MemberJoinDTO newUser);

    @Update("UPDATE user_info.user_info SET agreement_info = #{info}, agreement_finace = #{finance} WHERE id = #{id}")
    void updateAgree(@Param("info") boolean info, @Param("finance") boolean finance, @Param("id") String id);

    @Select("SELECT id FROM user_info.user_info WHERE email = #{email}")
    String findIdByEmail(String email);

    @Update("UPDATE user_info.user_info SET password = #{newPassword} WHERE id = #{id}")
    int resetPassword(@Param("id") String id, @Param("newPassword") String newPassword);
////Deletemember 추가함
//    void deleteMemberById(String id);

}
