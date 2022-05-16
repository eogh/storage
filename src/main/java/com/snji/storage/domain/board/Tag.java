package com.snji.storage.domain.board;

import com.snji.storage.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tag extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "TAG_ID")
    private Long id;

    @Column(nullable = false)
    private String name;
}
