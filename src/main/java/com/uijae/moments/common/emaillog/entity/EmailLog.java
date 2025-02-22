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

@Entity
@Table(name = "email_log")
@Getter
@Setter
public class EmailLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 254, nullable = false)
  private String recipient_email;

  @Enumerated(EnumType.STRING)
  private EmailStatus status;

  @Column(nullable = false)
  private int attempt_count = 0;  // 최대 5번

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
