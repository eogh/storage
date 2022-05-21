package com.snji.storage.domain.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snji.storage.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BoardFile extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "BOARD_FILE_ID")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String path;

    @Column(name = "THUMBNAIL_PATH")
    private String thumbnailPath;
}
