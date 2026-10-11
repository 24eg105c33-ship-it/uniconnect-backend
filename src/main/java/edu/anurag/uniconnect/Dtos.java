package edu.anurag.uniconnect;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class Dtos {
    private Dtos() {}

    public record RegisterReq(@NotBlank String name,
                              @NotBlank String email,
                              @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
                              String department,
                              Integer year) {}

    public record LoginReq(@NotBlank String email, @NotBlank String password) {}

    public record UserDto(Long id, String name, String email, String department, Integer year, String bio) {}

    public record AuthRes(String token, UserDto user) {}

    public record PostReq(String content, String imageUrl) {}

    public record PostDto(Long id, Long authorId, String authorName, String authorDepartment, Integer authorYear,
                          String content, String imageUrl, Instant createdAt,
                          long likes, long comments, int shares, boolean likedByMe) {}

    public record CommentReq(@NotBlank @Size(max = 1000) String text) {}

    public record CommentDto(Long id, String authorName, String text, Instant createdAt) {}
}
