package com.uijae.moments.post.repository;

import com.uijae.moments.post.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findByCreatedDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
