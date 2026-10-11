package edu.anurag.uniconnect;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface UserRepo extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findTop20ByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name, String dept);

    List<AppUser> findTop5ByIdNot(Long id);
}

interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
}

interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);
}

interface LikeRepo extends JpaRepository<PostLike, Long> {
    long countByPostId(Long postId);

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
}
