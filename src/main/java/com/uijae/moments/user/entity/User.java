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
  private String user_id;

  @Column(length = 5, nullable = false)
  private String name;

  @Column(unique = true, length = 254, nullable = false)
  private String email;

  @Column(length = 60, nullable = false)
  private String password;

  private LocalDateTime verified_date_time;

  @Column(updatable = false)
  private LocalDateTime created_date_time;

  private LocalDateTime updated_date_time;

  @PrePersist
  public void prePersist() {
    this.created_date_time = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updated_date_time = LocalDateTime.now();
  }
}
