package com.sideproject.damoim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sproject_recruit_group")
public class SProjectRecruitGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "post_idx")
    private SProjectPost post;

    @Column(name = "name")
    private String name;

    @Column(name = "recruit_count")
    private int recruitCount;
}
