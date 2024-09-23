package com.idle.kb_i_dle_backend.community.service;

import com.idle.kb_i_dle_backend.community.dto.BoardDTO;
import com.idle.kb_i_dle_backend.community.repository.CommunityRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

//    private final CommunityRepository communityRepository;
//
//    @Override
//    public List<BoardDTO> getAllBoard() {
//        return communityRepository.findAll().stream().map(board -> {
//            BoardDTO boardDTO = new BoardDTO();
//            boardDTO.setId(board.getId());
//            boardDTO.setTitle(board.getTitle());
//            boardDTO.setWriter(board.getWriter());
//            boardDTO.setContents(board.getContents());
//            return boardDTO;
//        }).collect(Collectors.toList());
//    }
}
