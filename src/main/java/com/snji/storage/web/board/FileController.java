package com.snji.storage.web.board;

import com.snji.storage.domain.board.File;
import com.snji.storage.domain.board.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    @Value("${storage.files.path}")
    private String filesPath;

    @Value("${storage.files.url}")
    private String filesUrl;

    private final FileRepository fileRepository;

    @PostMapping("/add")
    @ResponseBody
    public File add(@RequestParam("uploadfile") MultipartFile uploadfile) throws IOException {

        String originalFilename = uploadfile.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String newFilename = UUID.randomUUID().toString() + "." + ext;

        java.io.File ioFile = new java.io.File(filesPath + newFilename);
        uploadfile.transferTo(ioFile);
        // 썸네일

        File savedFile = File.builder()
                .filename(originalFilename)
                .path(filesUrl + newFilename)
                .build();

        return fileRepository.save(savedFile);
    }
}
