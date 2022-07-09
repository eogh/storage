package com.snji.storage.web.board;

import com.snji.storage.domain.board.BoardFile;
import com.snji.storage.domain.board.BoardFileRepository;
import com.snji.storage.domain.file.UploadFile;
import com.snji.storage.domain.file.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    private final BoardFileRepository boardFileRepository;
    private final UploadFileRepository uploadFileRepository;

    @PostMapping("/add")
    @ResponseBody
    public List<UploadFile> add(@RequestParam("uploadfiles") List<MultipartFile> multipartFiles) throws IOException {

        List<UploadFile> results = new ArrayList<>();

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowStr = now.format(formatter);

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            String storeFileName = createStoreFileName(originalFilename);
            String ext = extractExt(originalFilename);

            // original 파일 생성
            String dir = Paths.get("site", "original", nowStr).toString();
            Path path = Paths.get(filesPath, dir, storeFileName);
            String url = Paths.get(filesUrl, dir, storeFileName).toString();
            Files.createDirectories(Paths.get(filesPath, dir));     // 폴더가 없는 경우 생성
            multipartFile.transferTo(path.toFile());    // 파일 생성

            // thumbnail 파일 생성
            String thumbnailUrl = null;
            if (Objects.requireNonNull(multipartFile.getContentType()).startsWith("image")) {
                String thumbnailDir = Paths.get("site", "thumbnail", nowStr).toString();
                String thumbnailPath = Paths.get(filesPath, thumbnailDir, storeFileName).toString();
                thumbnailUrl = Paths.get(filesUrl, thumbnailDir, storeFileName).toString();
                Files.createDirectories(Paths.get(filesPath, thumbnailDir));
                createThumbnail(path.toString(), thumbnailPath, ext);
            }

            UploadFile savedFile = uploadFileRepository.save(UploadFile.builder()
                    .filename(originalFilename)
                    .path(url)
                    .thumbnailPath(thumbnailUrl)
                    .build());
            results.add(savedFile);
        }

        return results;
    }

    @DeleteMapping("{fileId}/delete")
    @ResponseBody
    public Long delete(@PathVariable Long fileId) {

        UploadFile file = uploadFileRepository.getById(fileId);

        File ioFile = new File(file.getPath());
        ioFile.delete();

        List<BoardFile> boardFiles = boardFileRepository.findAllByFile(file);
        for (BoardFile boardFile : boardFiles) {
            boardFileRepository.delete(boardFile);
        }
        uploadFileRepository.delete(file);

        return fileId;
    }

    public String createThumbnail(String originalFilePath, String thumbnailPath, String fileExt) throws IOException {
        // 저장된 원본파일로부터 BufferedImage 객체를 생성합니다.
        BufferedImage srcImg = ImageIO.read(new File(originalFilePath));

        // 썸네일의 너비와 높이 입니다.
        int dw = 250, dh = 150;

        // 원본 이미지의 너비와 높이 입니다.
        int ow = srcImg.getWidth();
        int oh = srcImg.getHeight();

        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
        int nw = ow; int nh = (ow * dh) / dw;

        // 계산된 높이가 원본보다 높다면 crop이 안되므로
        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
        if(nh > oh) {
            nw = (oh * dw) / dh;
            nh = oh;
        }

        // 계산된 크기로 원본이미지를 가운데에서 crop 합니다.
        BufferedImage cropImg = Scalr.crop(srcImg, (ow-nw)/2, (oh-nh)/2, nw, nh);

        // crop된 이미지로 썸네일을 생성합니다.
        BufferedImage destImg = Scalr.resize(cropImg, dw, dh);

        // 썸네일을 저장합니다. 이미지 이름 앞에 "THUMB_" 를 붙여 표시했습니다.
        File thumbFile = new File(thumbnailPath);
        ImageIO.write(destImg, fileExt.toLowerCase(), thumbFile);

        return thumbFile.getAbsolutePath();
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        if (originalFilename == null) {
            return null;
        }
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
