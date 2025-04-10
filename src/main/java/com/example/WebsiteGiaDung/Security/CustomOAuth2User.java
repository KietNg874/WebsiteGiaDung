package com.example.WebsiteGiaDung.Security;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;

public class CustomOAuth2User implements OAuth2User {
    private final OAuth2User delegate;
    private final String username;

    public CustomOAuth2User(OAuth2User delegate, String username) {
        this.delegate = delegate;
        this.username = username;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    public String getUsername() {
        return username;
    }
}
