package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, CustomBoardRepository {
    //List<Board> findAllByParentIsNull();
    List<Board> findAllByStatus(boolean status);
}
