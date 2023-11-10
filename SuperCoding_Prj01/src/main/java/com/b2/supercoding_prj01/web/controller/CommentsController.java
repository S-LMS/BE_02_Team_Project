package com.b2.supercoding_prj01.web.controller;

import com.b2.supercoding_prj01.dto.UserRequestDto;
import com.b2.supercoding_prj01.entity.CommentsEntity;
import com.b2.supercoding_prj01.entity.HeartEntity;
import com.b2.supercoding_prj01.entity.UserEntity;
import com.b2.supercoding_prj01.jwt.JwtTokenProvider;
import com.b2.supercoding_prj01.repository.CommentsRepository;
import com.b2.supercoding_prj01.repository.HeartRepository;
import com.b2.supercoding_prj01.repository.UserRepository;
import com.b2.supercoding_prj01.service.CommentsService;
import com.b2.supercoding_prj01.service.HeartService;
import com.b2.supercoding_prj01.service.UserService;
import com.b2.supercoding_prj01.web.dto.CommentsDto;
import com.b2.supercoding_prj01.web.dto.CommentsDto2;
import com.b2.supercoding_prj01.web.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentsController {
    private final CommentsRepository commentsRepository;
    private final HeartRepository heartRepository;
    private final HeartService heartService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentsService commentsService;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("")
    public List<CommentsDto> findAll(@RequestHeader("X-AUTH-TOKEN") String token){
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            List<CommentsEntity> commentsEntities = commentsService.findAll();
            return commentsEntities.stream()
                    .map(CommentsDto::fromEntity)
                    .collect(Collectors.toList());
        }else return null;
    }

    @GetMapping("/{boardId}")
    public List<CommentsDto> findByBoardId(@RequestHeader("X-AUTH-TOKEN") String token,
                                           @PathVariable long boardId){
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            List<CommentsEntity> commentsEntities = commentsService.findByAllBoardId(boardId);
            return commentsEntities.stream()
                    .map(CommentsDto::fromEntity)
                    .collect(Collectors.toList());
        }else return null;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentsDto2 commentsDto,
                                           @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            Optional<UserEntity> user = userRepository.findByEmail(author);

            if (user.isEmpty())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("없는 user입니다.");
            else {
                commentsDto.setAuthor(author);
                commentsDto.setUserId(user.get().getUserId());
                commentsService.saveComment(commentsDto);
                return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 작성되었습니다.");
            }
        }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그아웃된 사용자입니다.");
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updateComment(@PathVariable long postId,
                                           @RequestBody CommentsDto2 commentsDto,
                                           @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            Optional<CommentsEntity> comments = commentsRepository.findByPostId(postId);
            if (comments.isPresent()) {
                if (author.equals(commentsRepository.findByPostId(postId).get().getUser().getEmail())) {
                    commentsService.updateComment(commentsDto, postId);
                    return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 수정되었습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 가능한 댓글이 없습니다.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 가능한 댓글이 없습니다.");
            }
        }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그아웃된 사용자입니다.");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId,
                                           @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)){
            Optional<CommentsEntity> comments = commentsRepository.findByPostId(postId);
            if (comments.isPresent()) {
                if (author.equals(commentsRepository.findByPostId(postId).get().getUser().getEmail())) {
                    commentsService.deleteComment(postId);
                    return ResponseEntity.status(HttpStatus.OK).body("댓글이 성공적으로 삭제되었습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("다른 유저의 댓글입니다.");
                }
            }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 가능한 댓글이 없습니다.");
        }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그아웃된 사용자입니다.");
    }

    // 해당 댓글에 좋아요 추가
    @Transactional
    @PostMapping("/heart")
    public ResponseEntity<String> addHeart(@RequestHeader("X-AUTH-TOKEN") String token,
                                            @RequestParam Long postId
                                           ){
        if(token.isEmpty())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("token이 없습니다.");
        else {
            String email = jwtTokenProvider.findEmailBytoken(token);
            if(userService.test2(email)){
                return heartService.clickHeart(postId, email);
            }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그아웃된 사용자입니다.");
        }
    }

    // 해당 댓글에 좋아요 정보
    @GetMapping("/{postId}/heart")
    public List<HeartEntity> findHeartToPostId(@RequestHeader("X-AUTH-TOKEN") String token,
                                               @PathVariable Long postId){
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)){
        Optional<CommentsEntity> comments = commentsRepository.findByPostId(postId);

        return comments.map(heartRepository::findByComments).orElse(null);
//      = return comments.map(commentsEntity -> heartRepository.findByComments(commentsEntity)).orElse(null);
//      = if(comments.isPresent())
//            return heartRepository.findByComments(comments.get());
//        else return null;
        }else {
            return null;
        }
    }
}
