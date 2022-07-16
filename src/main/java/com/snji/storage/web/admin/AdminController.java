package com.snji.storage.web.admin;

import com.snji.storage.domain.board.Board;
import com.snji.storage.domain.board.BoardFile;
import com.snji.storage.domain.board.BoardFileRepository;
import com.snji.storage.domain.board.BoardRepository;
import com.snji.storage.domain.file.UploadFile;
import com.snji.storage.domain.file.UploadFileRepository;
import com.snji.storage.domain.file.UploadFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @Value("${storage.files.path}")
    private String filesPath;

    @Value("${storage.files.url}")
    private String filesUrl;

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UploadFileRepository uploadFileRepository;
    private final UploadFileService uploadFileService;

    @GetMapping
    public String admin() {
        return "admin/admin";
    }

    /**
     *
     * @return
     */
    @GetMapping("/api/board-sync")
    public String boardSync() throws IOException {

        Path originalDir = Paths.get(filesPath,"direct", "original");
        Pattern pattern = Pattern.compile("^-[0-9]{3}$");
//        final Instant maxCreationTime = LocalDateTime.of(2022, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);

        Map<Object, List<Path>> collect = Files.walk(originalDir)
                .filter(Files::isRegularFile)               // 일반적인 파일 필터
                .filter(path -> {                           // 특정시간 이후 생성된 파일 필터
                    try {
                        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                        return attributes.creationTime().toInstant().isAfter(LocalDateTime.now().minusMonths(1).toInstant(ZoneOffset.UTC));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .collect(Collectors.groupingBy(path -> {    // ex) -001, -002 등 으로 끝나는 파일명 끼리 Grouping
                    String toPath = path.toAbsolutePath().toString();
                    int posDash = toPath.lastIndexOf("-");
                    int posComma = toPath.lastIndexOf(".");

                    if (posDash > 0 && posComma > 0) {
                        String substring = toPath.substring(posDash, posComma);
                        if (pattern.matcher(substring).matches()) { // substring된 문자열이 정규식패턴과 일치하면 True
                            return toPath.substring(0, posDash);
                        }
                    }
                    return toPath;
                }));


        collect.forEach((group, paths) -> {
//            log.info("\n group : {}", group);
            Board board = boardRepository.save(Board.builder()
                    .title("board_" + LocalDate.now())
                    .build());

            for (Path path : paths) {
                String originalFilename = path.getFileName().toString();
                String storeFileName = uploadFileService.createStoreFileName(originalFilename);

                try {
//                    FileTime creationTime = (FileTime) Files.getAttribute(path, "creationTime");

                    String thumbnailPath = null;
                    if (true) { // TODO: getContentType 체크해서 image인 경우만 동작하도록
                        thumbnailPath = Paths.get(path.getParent().toString().replace("original", "thumbnail"), storeFileName).toString();
                        Files.createDirectories(Paths.get(path.getParent().toString().replace("original", "thumbnail")));
                        uploadFileService.createThumbnail(path.toAbsolutePath().toString(), thumbnailPath);
                    }

//                    log.info("path : {}", path.toAbsolutePath());
                    UploadFile uploadFile = uploadFileRepository.save(UploadFile.builder()
                            .filename(path.getFileName().toString())
                            .path(uploadFileService.pathToUrl(path.toAbsolutePath().toString()))
                            .thumbnailPath(uploadFileService.pathToUrl(thumbnailPath))
                            .build());

                    boardFileRepository.save(BoardFile.builder().board(board).file(uploadFile).build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // TODO: 전부 성공하면, 파일중에 생성시간이 가장 늦은 값을 저장한다.

        return "admin/admin";
    }
}
