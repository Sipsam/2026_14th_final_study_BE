package com.likelion.finalstudy.global.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey;
    private AccessToken accessToken = new AccessToken();

    @Getter
    @Setter
    public static class AccessToken {
        private String header = "Authorization";
        private long expiration;
    }
}

