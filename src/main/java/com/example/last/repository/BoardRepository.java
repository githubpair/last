package com.example.last.repository;

import com.example.last.dto.BoardDto;
import com.example.last.entity.Board;
import com.example.last.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAll(Pageable pageable);
    Page<Board> findByUser(Pageable pageable, User user);

}
