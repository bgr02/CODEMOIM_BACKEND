package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.Tag;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.dto.PostInfoDto;
import com.sideproject.codemoim.dto.PostWithCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomPostRepository {
    Optional<Post> searchPostByIdAndStatus(Long id);
    boolean relationTagExist(Long id);
    PostDto searchPost(Long postId);
    Page<PostDto> searchPostList(Pageable pageable, String type, Long boardId);
    PostInfoDto searchPostInfo(Long postId, Long profileId);
    Optional<Post> searchPostByIdAndProfileIdAndStatus(long postId, long profileId);
    long getScrapCount(Long id);
    boolean postExist();
    Page<PostDto> searchTagPost(Pageable pageable, String type, Long id);
    List<PostDto> searchDashboardFixedPostList(String type);
    List<Map<String, Object>> searchDashboardNonFixedPostList();
    List<Profile> searchScrapUserByPostId(Long id);
    Page<PostDto> searchPostListByProfileId(Pageable pageable, Long id);
    Page<PostWithCommentDto> searchCommentPostListByProfileId(Pageable pageable, Long id);
    Page<PostDto> searchScrapListByProfileId(Pageable pageable, Long id);
}
