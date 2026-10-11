package edu.anurag.uniconnect;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(nullable = false)
    public String passwordHash;

    public String department;
    // "year" is a reserved word in some databases (H2), so use a safe column name
    @Column(name = "year_of_study")
    public Integer yearOfStudy;

    @Column(length = 500)
    public String bio;
}
