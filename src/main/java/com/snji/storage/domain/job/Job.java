package com.snji.storage.domain.job;

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
public class Job extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "JOB_ID")
    private Long id;

    @Column(nullable = false)
    private String key;

    @Column
    private String value;

    @Column(nullable = false)
    private boolean completed;
}
