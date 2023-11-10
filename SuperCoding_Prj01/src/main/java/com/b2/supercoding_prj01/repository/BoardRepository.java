package com.b2.supercoding_prj01.repository;

import com.b2.supercoding_prj01.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    List<BoardEntity> findByEmail(String email);

}
