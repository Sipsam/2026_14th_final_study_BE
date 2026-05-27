package com.likelion.finalstudy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class AuthControllerTest {

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
    @DisplayName("회원가입 성공")
    void signupSuccess() throws Exception {
        Map<String, Object> request = Map.of(
                "email", "test@example.com",
                "password", "password1234",
                "name", "홍길동"
        );

        ResponseEntity<String> response = postJson("/api/auth/signup", request, null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertTrue(body.path("success").asBoolean());
        assertEquals(200, body.path("code").asInt());
        assertEquals("회원가입에 성공했습니다.", body.path("message").asText());
        assertEquals("test@example.com", body.path("data").path("email").asText());
        assertEquals("홍길동", body.path("data").path("name").asText());
    }

    @Test
    @DisplayName("중복 이메일 회원가입 실패")
    void signupFailDuplicatedEmail() throws Exception {
        signup("test@example.com", "password1234", "홍길동");

        Map<String, Object> request = Map.of(
                "email", "test@example.com",
                "password", "password1234",
                "name", "테스터"
        );

        ResponseEntity<String> response = postJson("/api/auth/signup", request, null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(409, response.getStatusCode().value());
        assertFalse(body.path("success").asBoolean());
        assertEquals(409, body.path("code").asInt());
        assertEquals("이미 사용 중인 이메일입니다.", body.path("message").asText());
        assertTrue(body.path("data").isNull());
    }

    @Test
    @DisplayName("잘못된 이메일 형식 회원가입 실패")
    void signupFailInvalidEmailFormat() throws Exception {
        Map<String, Object> request = Map.of(
                "email", "invalid-email",
                "password", "password1234",
                "name", "홍길동"
        );

        ResponseEntity<String> response = postJson("/api/auth/signup", request, null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(400, response.getStatusCode().value());
        assertFalse(body.path("success").asBoolean());
        assertEquals(400, body.path("code").asInt());
        assertEquals("email: 이메일 형식이 올바르지 않습니다.", body.path("message").asText());
        assertTrue(body.path("data").isNull());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        signup("test@example.com", "password1234", "홍길동");

        ResponseEntity<String> response = postJson("/api/auth/login", Map.of(
                "email", "test@example.com",
                "password", "password1234"
        ), null);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertTrue(jsonNode.path("success").asBoolean());
        assertEquals(200, jsonNode.path("code").asInt());
        assertFalse(jsonNode.path("data").path("accessToken").isNull());
        assertFalse(jsonNode.path("data").path("refreshToken").isNull());

        long userId = jsonNode.path("data").path("userId").asLong();
        String refreshToken = jsonNode.path("data").path("refreshToken").asText();

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        saved -> org.junit.jupiter.api.Assertions.assertEquals(refreshToken, saved.getToken()),
                        () -> org.junit.jupiter.api.Assertions.fail("refreshToken이 DB에 저장되지 않았습니다.")
                );
    }

    @Test
    @DisplayName("존재하지 않는 이메일 로그인 실패")
    void loginFailUserNotFound() throws Exception {
        ResponseEntity<String> response = postJson("/api/auth/login", Map.of(
                "email", "none@example.com",
                "password", "password1234"
        ), null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(404, response.getStatusCode().value());
        assertFalse(body.path("success").asBoolean());
        assertEquals(404, body.path("code").asInt());
        assertEquals("사용자를 찾을 수 없습니다.", body.path("message").asText());
    }

    @Test
    @DisplayName("비밀번호 불일치 로그인 실패")
    void loginFailInvalidPassword() throws Exception {
        signup("test@example.com", "password1234", "홍길동");

        ResponseEntity<String> response = postJson("/api/auth/login", Map.of(
                "email", "test@example.com",
                "password", "wrongpassword"
        ), null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(400, response.getStatusCode().value());
        assertFalse(body.path("success").asBoolean());
        assertEquals(400, body.path("code").asInt());
        assertEquals("비밀번호가 올바르지 않습니다.", body.path("message").asText());
    }

    @Test
    @DisplayName("Refresh Token 재발급 성공")
    void reissueSuccess() throws Exception {
        signup("test@example.com", "password1234", "홍길동");
        JsonNode loginData = login("test@example.com", "password1234").path("data");

        String refreshToken = loginData.path("refreshToken").asText();

        ResponseEntity<String> response = postJson("/api/auth/reissue", Map.of("refreshToken", refreshToken), null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertTrue(body.path("success").asBoolean());
        assertEquals(200, body.path("code").asInt());
        assertEquals("토큰 재발급에 성공했습니다.", body.path("message").asText());
        assertFalse(body.path("data").path("accessToken").isNull());
        assertFalse(body.path("data").path("refreshToken").isNull());
    }

    @Test
    @DisplayName("잘못된 Refresh Token 재발급 실패")
    void reissueFailInvalidToken() throws Exception {
        ResponseEntity<String> response = postJson("/api/auth/reissue", Map.of("refreshToken", "invalid.token"), null);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(401, response.getStatusCode().value());
        assertFalse(body.path("success").asBoolean());
        assertEquals(401, body.path("code").asInt());
        assertTrue(body.path("data").isNull());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        signup("test@example.com", "password1234", "홍길동");
        JsonNode loginData = login("test@example.com", "password1234").path("data");

        String accessToken = loginData.path("accessToken").asText();
        long userId = loginData.path("userId").asLong();

        ResponseEntity<String> response = postJson("/api/auth/logout", null, accessToken);
        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(200, response.getStatusCode().value());
        assertTrue(body.path("success").asBoolean());
        assertEquals(200, body.path("code").asInt());
        assertEquals("로그아웃에 성공했습니다.", body.path("message").asText());
        assertTrue(body.path("data").isNull());

        org.junit.jupiter.api.Assertions.assertTrue(refreshTokenRepository.findByUserId(userId).isEmpty());
    }

    private void signup(String email, String password, String name) throws Exception {
        ResponseEntity<String> response = postJson("/api/auth/signup", Map.of(
                "email", email,
                "password", password,
                "name", name
        ), null);
        assertEquals(200, response.getStatusCode().value());
    }

    private JsonNode login(String email, String password) throws Exception {
        ResponseEntity<String> response = postJson("/api/auth/login", Map.of(
                "email", email,
                "password", password
        ), null);
        assertEquals(200, response.getStatusCode().value());
        return objectMapper.readTree(response.getBody());
    }

    private ResponseEntity<String> postJson(String path, Object requestBody, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            headers.set("Authorization", "Bearer " + accessToken);
        }

        String body = requestBody == null ? null : toJson(requestBody);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
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


