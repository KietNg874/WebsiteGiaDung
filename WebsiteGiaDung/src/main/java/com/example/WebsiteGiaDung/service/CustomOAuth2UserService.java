package com.example.WebsiteGiaDung.service;

import com.example.WebsiteGiaDung.Security.CustomOAuth2User;
import com.example.WebsiteGiaDung.model.User;
import com.example.WebsiteGiaDung.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Extract user details from OAuth2 provider
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");

        // Check if the user exists in the database
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Save the user to the database if not exists
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(""); // Optional: generate a placeholder password
            user.setEnabled(false); // Default to false; adjust based on requirements
            user.setProvider("GOOGLE");
            userRepository.save(user);
        }

        // Wrap the OAuth2User into a custom implementation
        return new CustomOAuth2User(oAuth2User, username);
    }
}
