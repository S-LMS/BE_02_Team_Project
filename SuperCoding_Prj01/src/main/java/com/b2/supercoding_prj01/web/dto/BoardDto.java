package com.b2.supercoding_prj01.web.dto;

import com.b2.supercoding_prj01.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardDto {
    private UserEntity user;
    private String email;
    private String title;
    private String author;

    private String content;
}
