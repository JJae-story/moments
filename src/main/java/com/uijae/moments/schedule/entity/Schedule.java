package com.uijae.moments.schedule.entity;

import com.uijae.moments.group.entity.Group;
import com.uijae.moments.vote.entity.Vote;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDate schedule_date;

  @Column(updatable = false)
  private LocalDateTime created_date_time;

  private LocalDateTime updated_date_time;

  @ManyToOne
  @JoinColumn(name = "group_id", nullable = false)
  private Group group;

  @ManyToOne
  @JoinColumn(name = "vote_id", nullable = false)
  private Vote vote;

  @PrePersist
  public void prePersist() {
    this.created_date_time = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updated_date_time = LocalDateTime.now();
  }
}
