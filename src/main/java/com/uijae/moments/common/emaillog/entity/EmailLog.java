package com.uijae.moments.common.emaillog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Table(name = "email_log")
@Entity
@Getter
@Setter
public class EmailLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 254, nullable = false)
  private String recipientEmail;

  @Enumerated(EnumType.STRING)
  private EmailStatus status;

  @Column(nullable = false)
  private int attemptCount = 0;  // 최대 5번

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
