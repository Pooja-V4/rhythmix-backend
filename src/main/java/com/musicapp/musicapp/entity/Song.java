package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "songs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    private String album;

    private Integer durationSeconds;

    // The "other side" of the Playlist<->Song relationship
    // mappedBy = "songs" refers to the "songs" field in Playlist
    @ManyToMany(mappedBy = "songs")
    private List<Playlist> playlists;
}
