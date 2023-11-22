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
@Table(name = "sproject_reply")
public class SProjectReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "user_idx")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_idx")
    private SProjectPost sProjectPost;

    @Column(name = "content")
    private String content;

    @Column(name = "parent_reply_idx")
    private Long parentReplyIdx;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
