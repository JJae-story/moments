package com.uijae.moments.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 12, nullable = false)
  private String userId;

  @Column(length = 5, nullable = false)
  private String name;

  @Column(unique = true, length = 254, nullable = false)
  private String email;

  @Column(length = 60, nullable = false)
  private String password;

  private LocalDateTime verifiedDateTime;

  @Column(updatable = false)
  private LocalDateTime createdDateTime;

  private LocalDateTime updatedDateTime;

  @PrePersist
  public void prePersist() {
    this.createdDateTime = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedDateTime = LocalDateTime.now();
  }
}
