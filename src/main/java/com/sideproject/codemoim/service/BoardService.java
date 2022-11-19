package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Board;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.dto.BoardDto;
import com.sideproject.codemoim.dto.BoardListDto;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.BoardNotFoundException;
import com.sideproject.codemoim.exception.BoardRelationException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.BoardRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final ProfileRepository profileRepository;

    public List<BoardListDto> searchTreeList(String type) {
        List<Board> boardList = boardRepository.searchTreeList(type);

        if (!boardList.isEmpty()) {
            return boardList.stream().map(BoardListDto::new).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public BoardDto searchBoardInfo(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> {
            throw new BoardNotFoundException("Board Not Found");
        });

        BoardDto boardDto = new BoardDto(board);

        boolean subBoardExist = boardRepository.subBoardExist(id);

        boardDto.setSubBoardExist(subBoardExist);

        return boardDto;
    }

    public boolean searchBoardDeletableCheck(Long id) {
        boolean subBoardExist = boardRepository.subBoardExist(id);
        boolean postExist = boardRepository.postExist(id);

        return !subBoardExist && !postExist;
    }

    public List<BoardDto> searchAllList() {
        List<Board> boardList = boardRepository.findAllByStatus(true);

        List<BoardDto> boardDtoList = new ArrayList<>();

        if (!boardList.isEmpty()) {
            List<Board> filterBoard = boardList.stream().filter(board -> {
                boolean postExist = boardRepository.postExist(board.getId());

                return !postExist;
            }).collect(Collectors.toList());

            boardDtoList = filterBoard.stream().map(BoardDto::new).collect(Collectors.toList());
        }

        return boardDtoList;
    }

    public List<BoardDto> searchSubList() {
        List<Board> boardList = boardRepository.searchSubList();

        if (!boardList.isEmpty()) {
            return boardList.stream().map(BoardDto::new).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void createBoard(Map<String, Object> boardInfo, Long userId) {
        long profileId = (int) boardInfo.get("profileId");
        long parent = (int) boardInfo.get("parent");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                if (parent != 0L) {
                    Optional<Board> optionalBoard = boardRepository.searchByIdAndStatus(parent);

                    optionalBoard.ifPresentOrElse(parentBoard -> {
                        Board board = Board.builder()
                                .parent(parentBoard)
                                .name((String) boardInfo.get("name"))
                                .status((int) boardInfo.get("status") != 0)
                                .url((String) boardInfo.get("url"))
                                .icon((String) boardInfo.get("icon"))
                                .sort((int) boardInfo.get("sort"))
                                .display((int) boardInfo.get("display") != 0)
                                .sortDisplay((long) (int) boardInfo.get("sortDisplay"))
                                .authority((String) boardInfo.get("authority"))
                                .build();

                        boardRepository.save(board);
                    }, () -> {
                        throw new BoardNotFoundException("Board Not Found.");
                    });
                } else {
                    Board board = Board.builder()
                            .name((String) boardInfo.get("name"))
                            .status((int) boardInfo.get("status") != 0)
                            .url((String) boardInfo.get("url"))
                            .icon((String) boardInfo.get("icon"))
                            .sort((int) boardInfo.get("sort"))
                            .display((int) boardInfo.get("display") != 0)
                            .sortDisplay((long) (int) boardInfo.get("sortDisplay"))
                            .authority((String) boardInfo.get("authority"))
                            .build();

                    boardRepository.save(board);
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    @Transactional
    public void modifyBoard(Map<String, Object> boardInfo, Long userId) {
        long id = (int) boardInfo.get("id");
        long profileId = (int) boardInfo.get("profileId");
        long parentId = (int) boardInfo.get("parent");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                Board board = boardRepository.findById(id).orElseThrow(() -> {
                    throw new BoardNotFoundException("Board Not Found.");
                });

                if (parentId != 0L) {
                    Board parent = boardRepository.findById(parentId).orElseThrow(() -> {
                        throw new BoardNotFoundException("Board Not Found.");
                    });

                    if (parent.getDisplay()) {
                        parent.updateDisplay(false);
                        parent.updateSortDisplay(null);
                    }

                    board.updateParent(parent);
                } else {
                    if (board.getParent() != null) {
                        board.updateParent(null);
                    }
                }

                board.updateName((String) boardInfo.get("name"));
                board.updateStatus((int) boardInfo.get("status") != 0);
                board.updateUrl((String) boardInfo.get("url"));
                board.updateIcon((String) boardInfo.get("icon"));
                board.updateSort((int) boardInfo.get("sort"));
                board.updateDisplay((int) boardInfo.get("display") != 0);

                if ((int) boardInfo.get("display") != 0) {
                    board.updateSortDisplay((long) (int) boardInfo.get("sortDisplay"));
                } else {
                    board.updateSortDisplay(null);
                }

                board.updateAuthority((String) boardInfo.get("authority"));
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

    @Transactional
    public void deleteBoard(Map<String, Object> boardInfo, Long userId) {
        long id = (int) boardInfo.get("id");
        long profileId = (int) boardInfo.get("profileId");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                boolean subBoardExist = boardRepository.subBoardExist(id);
                boolean postExist = boardRepository.postExist(id);

                if (!subBoardExist && !postExist) {
                    Optional<Board> optionalBoard = boardRepository.findById(id);

                    optionalBoard.ifPresentOrElse(board -> {
                        boardRepository.delete(board);
                    }, () -> {
                        throw new BoardNotFoundException("Board Not Found.");
                    });
                } else {
                    throw new BoardRelationException("There are data related to boards.");
                }
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

}
