package com.snji.storage.web.board;

import com.snji.storage.domain.board.BoardFile;
import com.snji.storage.domain.board.BoardFileRepository;
import com.snji.storage.domain.file.UploadFile;
import com.snji.storage.domain.file.UploadFileRepository;
import com.snji.storage.domain.file.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    @Value("${storage.files.path}")
    private String filesPath;

    @Value("${storage.files.url}")
    private String filesUrl;

    private final BoardFileRepository boardFileRepository;
    private final UploadFileRepository uploadFileRepository;
    private final UploadFileService uploadFileService;

    @PostMapping("/add")
    @ResponseBody
    public List<UploadFile> add(@RequestParam("uploadfiles") List<MultipartFile> multipartFiles) throws IOException {

        List<UploadFile> results = new ArrayList<>();

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowStr = now.format(formatter);

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            String storeFileName = uploadFileService.createStoreFileName(originalFilename);

            // original 파일 생성
            String dir = Paths.get("site", "original", nowStr).toString();
            Path path = Paths.get(filesPath, dir, storeFileName);
            Files.createDirectories(Paths.get(filesPath, dir));     // 폴더가 없는 경우 생성
            multipartFile.transferTo(path.toFile());    // 파일 생성

            // thumbnail 파일 생성
            String thumbnailPath = null;
            if (Objects.requireNonNull(multipartFile.getContentType()).startsWith("image")) {
                String thumbnailDir = Paths.get("site", "thumbnail", nowStr).toString();
                thumbnailPath = Paths.get(filesPath, thumbnailDir, storeFileName).toString();
                Files.createDirectories(Paths.get(filesPath, thumbnailDir));
                uploadFileService.createThumbnail(path.toString(), thumbnailPath);
            }

            UploadFile savedFile = uploadFileRepository.save(UploadFile.builder()
                    .filename(originalFilename)
                    .path(uploadFileService.pathToUrl(path.toString()))
                    .thumbnailPath(uploadFileService.pathToUrl(thumbnailPath))
                    .build());
            results.add(savedFile);
        }

        return results;
    }

    @DeleteMapping("{fileId}/delete")
    @ResponseBody
    public Long delete(@PathVariable Long fileId) {

        UploadFile uploadFile = uploadFileRepository.getById(fileId);

        File file = new File(uploadFile.getPath());
        file.delete();

        List<BoardFile> boardFiles = boardFileRepository.findAllByFile(uploadFile);
        for (BoardFile boardFile : boardFiles) {
            boardFileRepository.delete(boardFile);
        }
        uploadFileRepository.delete(uploadFile);

        return fileId;
    }


}
