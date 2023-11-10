package com.b2.supercoding_prj01.service;

import com.b2.supercoding_prj01.entity.BoardEntity;
import com.b2.supercoding_prj01.entity.UserEntity;
import com.b2.supercoding_prj01.repository.BoardRepository;
import com.b2.supercoding_prj01.repository.UserRepository;
import com.b2.supercoding_prj01.web.dto.BoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    public String updateBoard(Long boardId, BoardDto boardDto, String email) {
        Optional<BoardEntity> board = boardRepository.findById(boardId);
        if(board.isPresent()){
            if (board.get().getUser().getEmail().equals(email)) {
                board.get().update(boardDto.getTitle(), boardDto.getContent());
                boardRepository.save(board.get());
            return "수정 완료되었습니다.";
          }else return "다른 유저의 게시글입니다.";
        }else return "없는 게시글입니다.";
    }


    public BoardEntity createBoard(BoardDto boardDto, String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            BoardEntity board = BoardEntity.builder()
                                            .createdAt(currentTimestamp)
                                            .title(boardDto.getTitle())
                                            .content(boardDto.getContent())
                                            .email(email)
                                            .author(boardDto.getAuthor())
                                            .user(user.get())
                                            .build();
            return boardRepository.save(board);
        }else return null;
    }
}
