package edu.anurag.uniconnect;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long postId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    public AppUser author;

    @Column(name = "body", length = 1000)
    public String text;

    public Instant createdAt = Instant.now();
}
