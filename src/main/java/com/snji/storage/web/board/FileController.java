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
import java.util.ArrayList;
import java.util.List;
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
    public List<File> add(@RequestParam("uploadfiles") List<MultipartFile> files) throws IOException {

        List<File> results = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String newFilename = UUID.randomUUID().toString() + "." + ext;

            java.io.File ioFile = new java.io.File(filesPath + newFilename);
            file.transferTo(ioFile);

            // TODO: 썸네일처리

            File savedFile = fileRepository.save(File.builder()
                    .filename(originalFilename)
                    .path(filesUrl + newFilename)
                    .build());
            results.add(savedFile);
        }

        return results;
    }
}
