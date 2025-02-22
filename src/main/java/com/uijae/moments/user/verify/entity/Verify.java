package com.uijae.moments.user.verify.entity;

import com.uijae.moments.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "verify")
@Getter
@Setter
public class Verify {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, length = 36, nullable = false)
  private String token;

  @Enumerated(EnumType.STRING)
  private TokenStatus status;

  @Column(updatable = false)
  private LocalDateTime created_date_time;

  @Column(nullable = false)
  private LocalDateTime expiry_date_time;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @PrePersist
  public void prePersist() {
    this.created_date_time = LocalDateTime.now();
  }
}
