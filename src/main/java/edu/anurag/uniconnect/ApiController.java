package edu.anurag.uniconnect;

import edu.anurag.uniconnect.Dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final int MAX_IMAGE_CHARS = 2_000_000; // ~1.5 MB image as data URL

    private final UserRepo users;
    private final PostRepo posts;
    private final CommentRepo comments;
    private final LikeRepo likes;

    ApiController(UserRepo users, PostRepo posts, CommentRepo comments, LikeRepo likes) {
        this.users = users;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
    }

    private static Long uid(HttpServletRequest r) {
        return (Long) r.getAttribute("uid");
    }

    private PostDto toDto(Post p, Long me) {
        return new PostDto(p.id, p.author.id, p.author.name, p.author.department, p.author.yearOfStudy,
                p.content, p.imageUrl, p.createdAt,
                likes.countByPostId(p.id), comments.countByPostId(p.id), p.shareCount,
                likes.findByPostIdAndUserId(p.id, me).isPresent());
    }

    private Post post(Long id) {
        return posts.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/me")
    public UserDto me(HttpServletRequest r) {
        return AuthController.toDto(users.findById(uid(r))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in again")));
    }

    @GetMapping("/posts")
    public List<PostDto> feed(HttpServletRequest r) {
        Long me = uid(r);
        return posts.findAllByOrderByCreatedAtDesc().stream().map(p -> toDto(p, me)).toList();
    }

    @PostMapping("/posts")
    public PostDto create(@RequestBody PostReq req, HttpServletRequest r) {
        boolean noText = req.content() == null || req.content().isBlank();
        boolean noImage = req.imageUrl() == null || req.imageUrl().isBlank();
        if (noText && noImage) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Write something or add a photo");
        }
        if (!noImage && (req.imageUrl().length() > MAX_IMAGE_CHARS || !req.imageUrl().startsWith("data:image/"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is too large or invalid");
        }
        Post p = new Post();
        p.author = users.findById(uid(r)).orElseThrow();
        p.content = noText ? null : req.content().trim();
        p.imageUrl = noImage ? null : req.imageUrl();
        return toDto(posts.save(p), uid(r));
    }

    @PostMapping("/posts/{id}/like")
    @Transactional
    public PostDto toggleLike(@PathVariable Long id, HttpServletRequest r) {
        Post p = post(id);
        Long me = uid(r);
        var existing = likes.findByPostIdAndUserId(id, me);
        if (existing.isPresent()) {
            likes.delete(existing.get());
        } else {
            PostLike l = new PostLike();
            l.postId = id;
            l.userId = me;
            likes.save(l);
        }
        likes.flush();
        return toDto(p, me);
    }

    @PostMapping("/posts/{id}/share")
    @Transactional
    public PostDto share(@PathVariable Long id, HttpServletRequest r) {
        Post p = post(id);
        p.shareCount++;
        return toDto(posts.save(p), uid(r));
    }

    @GetMapping("/posts/{id}/comments")
    public List<CommentDto> listComments(@PathVariable Long id) {
        post(id);
        return comments.findByPostIdOrderByCreatedAtAsc(id).stream()
                .map(c -> new CommentDto(c.id, c.author.name, c.text, c.createdAt)).toList();
    }

    @PostMapping("/posts/{id}/comments")
    public CommentDto addComment(@PathVariable Long id, @Valid @RequestBody CommentReq req, HttpServletRequest r) {
        post(id);
        Comment c = new Comment();
        c.postId = id;
        c.author = users.findById(uid(r)).orElseThrow();
        c.text = req.text().trim();
        c = comments.save(c);
        return new CommentDto(c.id, c.author.name, c.text, c.createdAt);
    }

    @GetMapping("/users/search")
    public List<UserDto> search(@RequestParam(defaultValue = "") String q) {
        if (q.isBlank()) return List.of();
        return users.findTop20ByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(q.trim(), q.trim())
                .stream().map(AuthController::toDto).toList();
    }

    @GetMapping("/users/suggestions")
    public List<UserDto> suggestions(HttpServletRequest r) {
        return users.findTop5ByIdNot(uid(r)).stream().map(AuthController::toDto).toList();
    }
}
