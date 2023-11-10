package com.b2.supercoding_prj01.web.controller;


import com.b2.supercoding_prj01.entity.BoardEntity;
import com.b2.supercoding_prj01.exception.NotFoundException;
import com.b2.supercoding_prj01.jwt.JwtTokenProvider;
import com.b2.supercoding_prj01.repository.BoardRepository;
import com.b2.supercoding_prj01.service.BoardService;
import com.b2.supercoding_prj01.service.UserService;
import com.b2.supercoding_prj01.web.dto.BoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createPost(@RequestBody BoardDto boardDto,
                                             @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            BoardEntity createBoard = boardService.createBoard(boardDto, author);
            if (createBoard != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("게시물이 생성되었습니다");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 생성에 실패했습니다");
            }
        }else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 생성에 실패했습니다");
    }

    // 모든 게시물 조회
    @GetMapping("/all")
    public List<BoardEntity> getAllPosts(@RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            return boardRepository.findAll();
        }else return null;
    }

    // 게시물 조회 by ID
    @GetMapping("/byBoardId")
    public ResponseEntity<?> getPostById(@RequestParam Long boardId,
                                                   @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            Optional<BoardEntity> board = boardRepository.findById(boardId);
            return board.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//        = if (board.isPresent()) {
//            return ResponseEntity.ok(board.get());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
        }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그아웃된 유저입니다.");
    }

    // 게시물 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateBoard(@RequestParam Long boardId,
                                              @RequestBody BoardDto boardDto,
                                              @RequestHeader("X-AUTH-TOKEN") String token)  {

        String author = jwtTokenProvider.findEmailBytoken(token);
        if(userService.test2(author)) {
            try {
                String updateBoard = boardService.updateBoard(boardId, boardDto, author);
                return ResponseEntity.ok(updateBoard);
            } catch (NotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }else return ResponseEntity.notFound().build();
    }

    // 게시물 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestParam Long boardId,
                                        @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if (userService.test2(author)) {
            try {
                boardRepository.deleteById(boardId);
                return ResponseEntity.status(200).body(boardId + "번 게시물이 삭제되었습니다.");
            } catch (NotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }else return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그아웃된 유저입니다.");
    }

//    추가: 이메일로 게시물 검색 API
    @GetMapping("/byemail")
    public List<BoardEntity> getBoardByEmail(@RequestParam String email,
                                             @RequestHeader("X-AUTH-TOKEN") String token) {
        String author = jwtTokenProvider.findEmailBytoken(token);
        if (userService.test2(author)) {
            return boardRepository.findByEmail(email);
        }else return null;
    }
}
