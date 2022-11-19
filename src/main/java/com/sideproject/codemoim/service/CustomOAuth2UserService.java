package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.*;
import com.sideproject.codemoim.exception.Oauth2AuthenticationErrorException;
import com.sideproject.codemoim.exception.WithdrawalAccountException;
import com.sideproject.codemoim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final OauthInfoRepository oauthInfoRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return handleOAuth2User(userRequest, oAuth2User.getAttributes());
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User handleOAuth2User(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2UserInfo oauth2UserInfo = validateOAuth2User(registrationId, attributes);

        Optional<OauthInfo> optionalOauthInfo =
                oauthInfoRepository.findByPlatformUserIdAndProvider(oauth2UserInfo.getPlatformUserId(), oauth2UserInfo.getProvider());

        final OauthInfo[] oauthInfo = new OauthInfo[1];

        optionalOauthInfo.ifPresentOrElse(previousOauthInfo -> {
            if (previousOauthInfo.getUser().getStatus() != 2) {
                oauthInfo[0] = updateOauthInfo(previousOauthInfo, oauth2UserInfo);
            } else {
                //탈퇴 회원
                throw new WithdrawalAccountException("This account has previously been withdrawn.");
            }
        }, () -> {
            oauthInfo[0] = registerOauthRelateInfo(oauth2UserInfo);
        });

//        if(optionalOauthInfo.isPresent()) {
//            OauthInfo previousOauthInfo = optionalOauthInfo.get();
//
//            if(previousOauthInfo.getUser().getStatus() != 2) {
//                oauthInfo[0] = updateOauthInfo(previousOauthInfo, oauth2UserInfo);
//            } else {
//                throw new WithdrawalAccountException("This account has previously been withdrawn.");
//            }
//        } else {
//            oauthInfo[0] = registerOauthRelateInfo(oauth2UserInfo);
//        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        String userId = String.valueOf(oauthInfo[0].getUser().getId());

        return new CustomOauth2User(userId, registrationId, authorities, attributes); //success handler에서 authentication 객체로 받음.
    }

    private OauthInfo updateOauthInfo(OauthInfo previousOauthInfo, Oauth2UserInfo oauth2UserInfo) {
        OauthInfo updateOauthInfo = previousOauthInfo.updateOauthInfo(oauth2UserInfo);

        return oauthInfoRepository.save(updateOauthInfo);
    }

    private OauthInfo registerOauthRelateInfo(Oauth2UserInfo oauth2UserInfo) {
        User user = registerUser(oauth2UserInfo);
        //registerEmail(user);
        registerProfile(user, oauth2UserInfo);

        OauthInfo oauthInfo = OauthInfo.builder()
                .user(user)
                .platformUserId(oauth2UserInfo.getPlatformUserId())
                .nickname(oauth2UserInfo.getNickname())
                .profileImgUrl(oauth2UserInfo.getProfileImgUrl())
                .provider(oauth2UserInfo.getProvider())
                .build();

        return oauthInfoRepository.save(oauthInfo);
    }

    private User registerUser(Oauth2UserInfo oauth2UserInfo) {
        List<Role> roles = new ArrayList<>();
        Role role = roleRepository.findByName(RoleName.ROLE_USER);
        roles.add(role);

        Optional<User> userOptional = Optional.ofNullable(userRepository.usernameDuplicateCheck(oauth2UserInfo.getNickname()));

        final User[] user = new User[1];

        userOptional.ifPresentOrElse(userEntity -> {
            boolean duplicateFlag = true;
            String uniqueUsername = "";

            while(duplicateFlag) {
                //중복되는 유저이름이므로 식별자를 추가하여 유저이름을 생성
                String duplicateUsername = userEntity.getUsername();
                String identifier = UUID.randomUUID().toString().substring(0, 5);
                uniqueUsername = duplicateUsername + "_" + identifier;

                Optional<User> userOptional2 = Optional.ofNullable(userRepository.usernameDuplicateCheck(uniqueUsername));

                if(userOptional2.isEmpty()) {
                    duplicateFlag = false;
                }
            }

            user[0] = User.builder()
                    .username(uniqueUsername)
                    .status((byte) 0)
                    .roles(roles)
                    .build();
        }, () -> {
            user[0] = User.builder()
                    .username(oauth2UserInfo.getNickname())
                    .status((byte) 0)
                    .roles(roles)
                    .build();
        });

        return userRepository.save(user[0]);
    }

//    private void registerEmail(User user) {
//        Email email = Email.builder()
//                .user(user)
//                .email("")
//                .build();
//
//        emailRepository.save(email);
//    }

    private Profile registerProfile(User user, Oauth2UserInfo oauth2UserInfo) {
        Profile profile = Profile.builder()
                .user(user)
                .username(user.getUsername())
                .profileImgUrl(oauth2UserInfo.getProfileImgUrl())
                .contributionPoint(0)
                .build();

        return profileRepository.save(profile);
    }

    private Oauth2UserInfo validateOAuth2User(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("github")) {
            return GithubOauth2UserInfo.builder()
                    .registrationId(registrationId)
                    .attributes(attributes)
                    .build();
        } else if (registrationId.equalsIgnoreCase("kakao")) {
            return KakaoOauth2UserInfo.builder()
                    .registrationId(registrationId)
                    .attributes(attributes)
                    .build();
        } else if (registrationId.equalsIgnoreCase("naver")) {
            attributes = (Map<String, Object>) attributes.get("response");

            return NaverOauth2UserInfo.builder()
                    .registrationId(registrationId)
                    .attributes(attributes)
                    .build();
        } else {
            throw new Oauth2AuthenticationErrorException(registrationId + " is an unsupported authentication method.");
        }
    }

}
