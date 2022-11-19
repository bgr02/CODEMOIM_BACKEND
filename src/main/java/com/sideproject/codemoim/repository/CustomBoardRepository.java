package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Board;

import java.util.List;
import java.util.Optional;

public interface CustomBoardRepository {
    Optional<Board> searchByIdAndStatus(Long id);
    List<Board> searchTreeList(String type);
    boolean subBoardExist(Long id);
    boolean postExist(Long id);
    List<Board> searchSubList();
}
