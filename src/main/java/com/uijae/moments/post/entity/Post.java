package com.uijae.moments.post.entity;

import com.uijae.moments.post.image.entity.PostImage;
import com.uijae.moments.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String title;

  @Column(length = 1000)
  private String content;

  @Column(updatable = false)
  private LocalDateTime createdDateTime;

  private LocalDateTime updatedDateTime;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PostImage> postImages = new ArrayList<>();

  public void addImage(PostImage postImage) {
    postImages.add(postImage);
    postImage.setPost(this);
  }

  @PrePersist
  public void prePersist() {
    this.createdDateTime = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedDateTime = LocalDateTime.now();
  }
}
