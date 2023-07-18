package com.example.security1.config.auth.provider;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@NoArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String,Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String)attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getName() {
        return (String)attributes.get("nickname");
    }
}
