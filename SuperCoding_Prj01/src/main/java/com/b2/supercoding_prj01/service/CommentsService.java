package com.b2.supercoding_prj01.service;

import com.b2.supercoding_prj01.entity.BoardEntity;
import com.b2.supercoding_prj01.entity.CommentsEntity;
import com.b2.supercoding_prj01.entity.UserEntity;
import com.b2.supercoding_prj01.repository.BoardRepository;
import com.b2.supercoding_prj01.repository.CommentsRepository;
import com.b2.supercoding_prj01.repository.UserRepository;
import com.b2.supercoding_prj01.web.dto.CommentsDto2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());



    public List<CommentsEntity> findAll() {
        return commentsRepository.findAll();
    }

    public List<CommentsEntity> findByAllBoardId(long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 게시판을 찾을 수 없습니다. boardId=" + boardId));
        return commentsRepository.findByBoard(board);
    }

    public void saveComment(CommentsDto2 commentsDto) {
        BoardEntity board = boardRepository.findById(commentsDto.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 게시물을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByUserId(commentsDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        CommentsEntity commentsEntity = CommentsEntity.builder()
                .board(board)
                .author(commentsDto.getAuthor())
                .content(commentsDto.getContent())
                .user(user)
                .heart(0)
                .createdAt(currentTimestamp)
                .build();

        commentsRepository.save(commentsEntity);
    }
    public void updateComment(CommentsDto2 commentsDto, long postId) {
        Optional<CommentsEntity> existingComment = commentsRepository.findByPostId(postId);
        if(existingComment.isPresent()) {
            if (commentsDto.getBoardId().equals(existingComment.get().getBoard().getBoardId())) {
                CommentsEntity commentsEntity = existingComment.get();
                commentsEntity.setContent(commentsDto.getContent());
                commentsRepository.save(commentsEntity);
            } else throw new EntityNotFoundException("해당 게시물에 작성한 댓글이 없습니다.");
        }else throw new EntityNotFoundException("댓글을 찾을 수 없습니다.");
    }
    public void deleteComment(long postId) {
        Optional<CommentsEntity> existingComment = commentsRepository.findByPostId(postId);
        if (existingComment.isPresent())
            commentsRepository.deleteById(postId);
        else throw new EntityNotFoundException("댓글을 찾을 수 없습니다.");
    }

}
