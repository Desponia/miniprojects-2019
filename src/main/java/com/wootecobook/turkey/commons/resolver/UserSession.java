package com.wootecobook.turkey.commons.resolver;

import com.wootecobook.turkey.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserSession {

    public static final String USER_SESSION_KEY = "loginUser";

    private Long id;
    private String email;
    private String name;

    @Builder
    public UserSession(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public static UserSession from(User user) {
        return UserSession.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
