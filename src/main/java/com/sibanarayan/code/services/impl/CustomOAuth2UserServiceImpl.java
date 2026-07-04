package com.sibanarayan.code.services.impl;

import com.sibanarayan.code.entities.User;
import com.sibanarayan.code.enums.UserDetailProvider;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.services.UserService;
import com.sibanarayan.code.utility.CustomOAuthUser;
import com.sibanarayan.shared_package.enums.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class CustomOAuth2UserServiceImpl
        extends DefaultOAuth2UserService {

    private final UserService userService;


    @Override
    public CustomOAuthUser loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User user = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();

        UserDetailProvider provider = switch (registrationId.toLowerCase()) {
            case "google" -> UserDetailProvider.GOOGLE;
            case "github" -> UserDetailProvider.GITHUB;
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        };

        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(user.getAttribute("email"))
                .name(user.getAttribute("name"))
                .role(UserRole.USER)
                .userDetailProvider(provider)
                .build();

        User record=userService.createUser(createUserRequest);
        CustomOAuthUser auth=new CustomOAuthUser(record,user.getAttributes());
        return auth;
    }
}
