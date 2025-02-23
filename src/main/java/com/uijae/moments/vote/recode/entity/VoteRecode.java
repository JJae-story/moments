package com.uijae.moments.vote.recode.entity;

import com.uijae.moments.user.entity.User;
import com.uijae.moments.vote.option.entity.VoteOption;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class VoteRecode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "vote_option_id", nullable = false)
  private VoteOption voteOption;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
