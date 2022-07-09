package com.snji.storage.domain.board;

import com.snji.storage.domain.file.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
    List<BoardFile> findAllByFile(UploadFile file);
}
