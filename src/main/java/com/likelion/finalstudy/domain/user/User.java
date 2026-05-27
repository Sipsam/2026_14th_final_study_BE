package com.likelion.finalstudy.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends com.likelion.finalstudy.domain.BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 반드시 암호화(인코딩)된 비밀번호를 저장한다.
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private com.likelion.finalstudy.domain.user.Role role;

    @Builder(access = AccessLevel.PRIVATE)
    private User(Long id, String email, String password, String name, com.likelion.finalstudy.domain.user.Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public static User createUser(String email, String encodedPassword, String name, com.likelion.finalstudy.domain.user.Role role) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .role(role == null ? com.likelion.finalstudy.domain.user.Role.USER : role)
                .build();
    }
}

