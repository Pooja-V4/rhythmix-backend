package com.musicapp.musicapp.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment PK
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)   // Email must be unique
    private String email;

    @Column(nullable = false)
    private String password;    // Plain text for now — we'll hash it in Phase 5

    // One user can have MANY playlists
    // mappedBy = "user" means the "user" field in Playlist owns this relationship
    // cascade = ALL means if you delete a user, their playlists are also deleted
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists;

    // One user can have MANY favorites
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;
}
