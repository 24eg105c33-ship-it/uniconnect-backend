package edu.anurag.uniconnect;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    public AppUser author;

    @Column(columnDefinition = "TEXT")
    public String content;

    @Column(columnDefinition = "TEXT")
    public String imageUrl;

    public Instant createdAt = Instant.now();

    public int shareCount = 0;
}
