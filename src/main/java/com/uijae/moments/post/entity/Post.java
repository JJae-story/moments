package com.uijae.moments.post.entity;

import com.uijae.moments.post.image.entity.PostImage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "post")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

//  @ManyToOne
//  @JoinColumn(name = "user_id", nullable = false)
//  private User user;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
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
