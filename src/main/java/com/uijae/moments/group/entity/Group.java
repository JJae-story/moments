package com.uijae.moments.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 20, nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  private GroupType type;

  @Column(unique = true, length = 10, nullable = false)
  private String code;

  @Column(updatable = false)
  private LocalDateTime createdDateTime;

  @PrePersist
  public void prePersist() {
    this.createdDateTime = LocalDateTime.now();
  }
}
