package com.example.team16project.dto.user;

import com.example.team16project.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {
    private String email;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;

    public UserInfo(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.createdAt = user.getCreatedAt();
        this.profileImage = user.getProfileImage();
    }

    public UserInfo(String email, String nickname, String profileImage, LocalDateTime createdAt) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }
}