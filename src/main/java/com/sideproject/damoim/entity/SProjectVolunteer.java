package com.sideproject.damoim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sproject_volunteer")
public class SProjectVolunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "group_idx")
    private SProjectRecruitGroup sProjectRecruitGroup;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @Column(name = "result")
    private String result;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
