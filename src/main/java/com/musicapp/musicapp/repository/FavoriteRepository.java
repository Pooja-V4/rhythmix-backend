package com.musicapp.musicapp.repository;

import com.musicapp.musicapp.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    boolean existsByUserIdAndSongId(Long userId, Long songId);

    void deleteByUserIdAndSongId(Long userId, Long songId);
}