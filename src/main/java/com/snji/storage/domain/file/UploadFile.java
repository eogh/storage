package com.snji.storage.domain.file;

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
public class UploadFile extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "UPLOAD_FILE_ID")
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String path;

    @Column(name = "THUMBNAIL_PATH", nullable = true)
    private String thumbnailPath;
}
