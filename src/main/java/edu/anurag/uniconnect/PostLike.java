package edu.anurag.uniconnect;

import jakarta.persistence.*;

@Entity
@Table(name = "post_likes", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "post_id")
    public Long postId;

    @Column(name = "user_id")
    public Long userId;
}
