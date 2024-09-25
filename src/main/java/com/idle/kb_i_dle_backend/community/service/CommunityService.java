package com.idle.kb_i_dle_backend.community.service;

import com.idle.kb_i_dle_backend.community.dto.BoardDTO;
import java.util.List;

/**
 * service는 service interface와 구현체인 Impl로 할것
 * 개방 폐쇄의 원칙(OCP)에 기반한 전략 패턴을 사용하기 위함입니다. 개방 폐쇄의 원칙이란 확장에는 열려있고 변화에는 닫혀있다는 의미
 */
public interface CommunityService {

    List<BoardDTO> getAllBoard();
}
