package com.uijae.moments.album.entity;

import com.uijae.moments.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "album")
@Getter
public class Album {

  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(length = 50, nullable = false)
  private String title;

  @Column(length = 10)
  private String type;

  @Setter
  @Column(length = 100, nullable = false)
  private String summary;

  @Column(updatable = false)
  private LocalDate created_date;

  @Setter
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @PrePersist
  public void prePersist() {
    this.created_date = LocalDate.now();
  }

  public void setType(String type) {
    if (type == null || type.isEmpty()) {
      this.type = String.valueOf(LocalDate.now().getYear());
    } else {
      this.type = type;
    }
  }
}
