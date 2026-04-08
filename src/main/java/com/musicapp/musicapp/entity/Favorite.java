package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
        name = "favorites",
        // Prevents duplicate favorites (same user can't like the same song twice)
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many favorites belong to ONE user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"favorites", "playlists", "hibernateLazyInitializer"})
    private User user;

    // Many favorites point to ONE song
    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    @JsonIgnoreProperties({"playlists", "hibernateLazyInitializer"})
    private Song song;
}
