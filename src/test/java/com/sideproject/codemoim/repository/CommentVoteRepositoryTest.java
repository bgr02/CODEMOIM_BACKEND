package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentVoteRepositoryTest {

    @Autowired
    CommentVoteRepository commentVoteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostTagRepository postTagRepository;

    Comment saveComment;

    Profile saveProfile;

    CommentVote saveCommentVote;

    @BeforeEach
    void createCommentVote() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        saveProfile = profileRepository.save(profile);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(0)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Post savePost = postRepository.save(post);

        PostTag postTag = PostTag.builder()
                .post(savePost)
                .tag(saveTag)
                .build();

        postTagRepository.save(postTag);

        Comment comment = Comment.builder()
                .profile(saveProfile)
                .post(savePost)
                .content("test")
                .selectedComment(false)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        saveComment = commentRepository.save(comment);

        CommentVote commentVote = CommentVote.builder()
                .profile(saveProfile)
                .comment(saveComment)
                .voteCount(0)
                .build();

        saveCommentVote = commentVoteRepository.save(commentVote);
    }

    @Test
    @DisplayName("댓글 아이디와 프로필 아이디를 사용한 댓글 엔티티 조회 테스트")
    void searchCommentVoteByCommentIdAndProfileIdTest() {
        CommentVote findCommentVote = commentVoteRepository.searchCommentVoteByCommentIdAndProfileId(saveComment.getId(), saveProfile.getId());

        Assertions.assertNotNull(findCommentVote);
    }

    @Test
    @DisplayName("프로필 아이디를 사용한 코멘트 추천 조회 테스트")
    void searchCommentVoteByProfileIdTest() {
        List<CommentVote> commentVoteList = commentVoteRepository.searchCommentVoteByProfileId(saveProfile.getId());

        CommentVote searchCommentVote = commentVoteList.get(0);

        Assertions.assertEquals(saveCommentVote.getId(), searchCommentVote.getId());
    }

    @Test
    @DisplayName("코멘트를 사용한 코멘트 추천 삭제 테스트")
    void deleteCommentVoteByCommentTest() {
        int deleteCommentVoteCount = commentVoteRepository.deleteCommentVoteByComment(saveComment);

        Assertions.assertEquals(deleteCommentVoteCount, 1);
    }

}
