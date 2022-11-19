package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Role;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.repository.CustomSearchRepository;
import com.sideproject.codemoim.repository.PostRepository;
import com.sideproject.codemoim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final UserRepository userRepository;
    private final CustomSearchRepository searchRepository;
    private final PostRepository postRepository;

    public void initializeSearchIndex(Long userId) throws InterruptedException {
        User user = userRepository.searchUserByIdAndStatus(userId).orElseThrow(() -> {
            throw new UsernameNotFoundException("User Not Found");
        });

        List<Role> roles = user.getRoles();

        boolean adminRole = false;

        for (Role role : roles) {
            if (role.getName().name().equals("admin")) {
                adminRole = true;
                break;
            }
        }

        if (adminRole && postRepository.postExist()) {
            searchRepository.buildSearchIndex();
        }
    }

    public void buildSearchIndex() throws InterruptedException {
        if (postRepository.postExist()) {
            searchRepository.buildSearchIndex();
        }
    }

    public Page<PostDto> searchPostByKeyword(Pageable pageable, String keyword) throws UnsupportedEncodingException {
        String searchKeyword = keyword;

        if (specialWordExist(keyword)) {
            searchKeyword = URLDecoder.decode(searchKeyword, "UTF-8");
            //searchKeyword = addBackSlashSpecialWords(searchKeyword);
        }

        return searchRepository.searchPostByKeyword(pageable, searchKeyword);
    }

    public Page<PostDto> searchPostByKeywordAndBoardId(Pageable pageable, String keyword, Long boardId) throws UnsupportedEncodingException {
        String searchKeyword = keyword;

        if (specialWordExist(keyword)) {
            searchKeyword = URLDecoder.decode(searchKeyword, "UTF-8");
            //searchKeyword = addBackSlashSpecialWords(searchKeyword);
        }

        return searchRepository.searchPostByKeywordAndBoardId(pageable, searchKeyword, boardId);
    }

    public Page<PostDto> searchPostByKeywordAndTagName(Pageable pageable, String keyword, String tagName) throws UnsupportedEncodingException {
        String searchKeyword = keyword;
        String searchTagName = tagName;

        if (specialWordExist(keyword)) {
            searchKeyword = URLDecoder.decode(searchKeyword, "UTF-8");
            //searchKeyword = addBackSlashSpecialWords(searchKeyword);
        }

        if (specialWordExist(tagName)) {
            searchTagName = URLDecoder.decode(searchTagName, "UTF-8");
        }

        return searchRepository.searchPostByKeywordAndTagName(pageable, searchKeyword, searchTagName);
    }

    private boolean specialWordExist(String keyword) {
        boolean exist = false;

        String[] checkWords = {"%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29", "%2A", "%2B",
                "%2C", "%2D", "%2E", "%2F", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%5B", "%5C", "%5D",
                "%5E", "%5F", "%60", "%7B", "%7C", "%7D", "%7E"};

        for (String checkWord : checkWords) {
            if (keyword.contains(checkWord)) {
                exist = true;
                break;
            }
        }

        return exist;
    }

    private String addBackSlashSpecialWords(String keyword) {
        String[] specialWords = {" ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", ":",
                ";", "<", "=", ">", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~"};

        String[] backSlashSpecialWords = {"\\ ", "\\!", "\\\"", "\\#", "\\$", "\\%", "\\&", "\\'", "\\(", "\\)", "\\*", "\\+",
                "\\,", "\\-", "\\.", "\\/", "\\:", "\\;", "\\<", "\\=", "\\>", "\\?", "\\@", "\\[", "\\\\", "\\]",
                "\\^", "\\_", "\\`", "\\{", "\\|", "\\}", "\\~"};

        String changeKeyword = keyword;

        for (int i = 0; i < specialWords.length; i++) {
            String specialWord = specialWords[i];
            String backSlashSpecialWord = backSlashSpecialWords[i];

            if(changeKeyword.contains(specialWord)) {
                changeKeyword = changeKeyword.replace(specialWord, backSlashSpecialWord);
                changeKeyword = changeKeyword.replace("\\\\", "\\");
            }
        }

        return changeKeyword;
    }

}
