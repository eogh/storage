package com.snji.storage.domain.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snji.storage.domain.BaseTimeEntity;
import com.snji.storage.domain.file.UploadFile;
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FILE_ID")
    private UploadFile file;
}
