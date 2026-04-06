package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Many playlists belong to ONE user
    // @JoinColumn creates the foreign key column in this table
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many playlists can have MANY songs (and vice versa)
    // @JoinTable defines the join table that Hibernate creates automatically
    @ManyToMany
    @JoinTable(
            name = "playlist_songs",                          // Join table name
            joinColumns = @JoinColumn(name = "playlist_id"), // FK to this entity
            inverseJoinColumns = @JoinColumn(name = "song_id") // FK to Song
    )
    private List<Song> songs;
}
