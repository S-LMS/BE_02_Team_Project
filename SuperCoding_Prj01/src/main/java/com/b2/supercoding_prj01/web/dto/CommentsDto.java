package com.b2.supercoding_prj01.web.dto;

import com.b2.supercoding_prj01.entity.CommentsEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentsDto {
    @JsonProperty("postId")
    private Long postId;

    private String content;

    private String author;

    @JsonProperty("heart")
    private Integer heart;

    @JsonProperty("boardId")
    private Long boardId;

    @JsonProperty("createdAt")
    private Timestamp createdAt;


    public static CommentsDto fromEntity(CommentsEntity entity) {
        CommentsDto dto = new CommentsDto();
        dto.setPostId(entity.getPostId());
        dto.setContent(entity.getContent());
        dto.setAuthor(entity.getAuthor());
        dto.setBoardId(entity.getBoard().getBoardId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setHeart(entity.getHeart());
        return dto;
    }
}
