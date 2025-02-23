package com.uijae.moments.vote.option.entity;

import com.uijae.moments.vote.entity.Vote;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vote_option")
@Getter
@Setter
public class VoteOption {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String optionTitle;

  @Column(nullable = false)
  private int voteOptionTitle = 0;

  @ManyToOne
  @JoinColumn(name = "vote_id", nullable = false)
  private Vote vote;
}
