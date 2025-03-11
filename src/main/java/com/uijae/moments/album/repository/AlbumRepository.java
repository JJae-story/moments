package com.uijae.moments.album.repository;

import com.uijae.moments.album.entity.Album;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
  List<Album> findByGroupId(Long groupId);
}
