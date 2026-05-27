package com.likelion.finalstudy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.finalstudy.config.TestRestTemplateTestConfig;
import com.likelion.finalstudy.repository.RefreshTokenRepository;
import com.likelion.finalstudy.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
@Import(TestRestTemplateTestConfig.class)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("인증 없이 /api/users/me 접근 실패")
    void getMeUnauthorized() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl("/api/users/me"), String.class);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(401, response.getStatusCode().value());
        assertFalse(body.path("success").asBoolean());
        assertEquals(401, body.path("code").asInt());
        assertTrue(body.path("data").isNull());
    }

    @Test
    @DisplayName("유효한 Access Token으로 /api/users/me 접근 성공")
    void getMeSuccessWithValidToken() throws Exception {
        signup("test@example.com", "password1234", "홍길동");
        String accessToken = loginAndGetAccessToken("test@example.com", "password1234");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl("/api/users/me"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertTrue(body.path("success").asBoolean());
        assertEquals(200, body.path("code").asInt());
        assertEquals("내 정보 조회에 성공했습니다.", body.path("message").asText());
        assertEquals("test@example.com", body.path("data").path("email").asText());
        assertEquals("홍길동", body.path("data").path("name").asText());
        assertEquals("USER", body.path("data").path("role").asText());
        assertFalse(body.path("data").path("createdAt").isNull());
        assertTrue(body.path("data").path("password").isMissingNode());
    }

    private void signup(String email, String password, String name) throws Exception {
        ResponseEntity<String> response = postJson("/api/auth/signup", Map.of(
                "email", email,
                "password", password,
                "name", name
        ));
        assertEquals(200, response.getStatusCode().value());
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        ResponseEntity<String> response = postJson("/api/auth/login", Map.of(
                "email", email,
                "password", password
        ));
        assertEquals(200, response.getStatusCode().value());

        JsonNode node = objectMapper.readTree(response.getBody());
        return node.path("data").path("accessToken").asText();
    }

    private ResponseEntity<String> postJson(String path, Object requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(toJson(requestBody), headers);
        return restTemplate.exchange(baseUrl(path), HttpMethod.POST, entity, String.class);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}


