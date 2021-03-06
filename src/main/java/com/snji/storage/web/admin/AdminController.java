package com.snji.storage.web.admin;

import com.snji.storage.domain.board.*;
import com.snji.storage.domain.file.UploadFile;
import com.snji.storage.domain.file.UploadFileRepository;
import com.snji.storage.domain.file.UploadFileService;
import com.snji.storage.domain.job.Job;
import com.snji.storage.domain.job.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
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
    private final BoardTagRepository boardTagRepository;
    private final TagService tagService;
    private final BoardFileRepository boardFileRepository;
    private final UploadFileRepository uploadFileRepository;
    private final UploadFileService uploadFileService;
    private final JobRepository jobRepository;

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

        Job lastCompletedJob = jobRepository.findTopByJobKeyOrderByJobValueDesc("BOARD_SYNC");
        long lastCompletedJobTime = lastCompletedJob != null ? Long.parseLong(lastCompletedJob.getJobValue()) : 0L;
        log.info("lastCompletedJobTime : {}", lastCompletedJobTime);

        // ?????? ????????? ?????? ?????? ????????? ????????????.
        Map<Object, List<Path>> collect = Files.walk(originalDir)
                .filter(Files::isRegularFile)               // ???????????? ?????? ??????
                .filter(path -> {                           // ???????????? ?????? ????????? ?????? ??????
                    try {
                        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                        return attributes.creationTime().toMillis() > lastCompletedJobTime;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .sorted(Comparator.comparing(Path::toAbsolutePath))
                .collect(Collectors.groupingBy(path -> {    // ex) -001, -002 ??? ?????? ????????? ????????? ?????? Grouping
                    String toPath = path.toAbsolutePath().toString();
                    int posDash = toPath.lastIndexOf("-");

                    if (posDash > 0) {
                        String substring1 = toPath.substring(posDash); // ex) -001.jpg, -001.jpg.jpg
                        if (substring1.length() >= 4) {
                            String substring2 = substring1.substring(0, 4); // ex) -001, -002
                            if (pattern.matcher(substring2).matches()) { // substring??? ???????????? ?????????????????? ???????????? True
                                return toPath.substring(0, posDash);
                            }
                        }
                    }
                    return toPath;
                }));

        log.info("collect size : {}", collect.size());
        if (collect.size() == 0) return "redirect:/admin";

        Job jobInfo = jobRepository.save(Job.builder()
                .jobKey("BOARD_SYNC")
                .completed(false)
                .build());

        /*
        collect.forEach((group, paths) -> {
            String value = String.valueOf(group);
            String filename = value.substring(value.lastIndexOf(File.separator) + 1);
            log.info("filename : {}", filename);
            for (Path path : paths) {
                log.info("path : {}", path.toAbsolutePath());
                String[] folders = pathToFolders(path.toAbsolutePath().toString());
                for (String folder : folders) {
                    log.info(folder);
                }
            }
        });
        if (true) {
            return "redirect:/admin";
        }
        */

        // ????????? ????????? ???????????? ?????????.
        collect.forEach((group, paths) -> {
            String value = String.valueOf(group);
            String filename = value.substring(value.lastIndexOf(File.separator) + 1);

            Board board = boardRepository.save(Board.builder()
                    .title(filename)
                    .build());

            String[] folders = pathToFolders(value);
            for (String folder : folders) {
                Tag tag = tagService.add(folder);
                boardTagRepository.save(BoardTag.builder().board(board).tag(tag).build());
            }

            for (Path path : paths) {
                String originalFilename = path.getFileName().toString();
                String storeFileName = uploadFileService.createStoreFileName(originalFilename);

                try {
                    String thumbnailPath = null;
                    if (true) { // TODO: getContentType ???????????? image??? ????????? ???????????????
                        thumbnailPath = Paths.get(path.getParent().toString().replace("original", "thumbnail"), storeFileName).toString();
                        Files.createDirectories(Paths.get(path.getParent().toString().replace("original", "thumbnail")));
                        uploadFileService.createThumbnail(path.toAbsolutePath().toString(), thumbnailPath);
                    }

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

        // ????????? ?????? ??? ???????????? ?????? ?????? ????????? ????????? ????????????.
        Path lastCreatedPath = Files.walk(originalDir).max(Comparator.comparing(path -> {
            FileTime fileTime = null;
            try {
                fileTime = Files.readAttributes(path, BasicFileAttributes.class).creationTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileTime != null ? fileTime.toMillis() : 0;
        })).orElseThrow(NoSuchElementException::new);
        Long lastCreatedTime = Files.readAttributes(lastCreatedPath, BasicFileAttributes.class).creationTime().toMillis();

        jobInfo.setJobValue(String.valueOf(lastCreatedTime));
        jobInfo.setCompleted(true);
        jobRepository.flush();

        return "redirect:/admin";
    }

    /**
     * ???????????? ???????????? ????????????.
     * @param path
     * @return
     */
    private String[] pathToFolders(String path) {
        String splitRegex = Pattern.quote(System.getProperty("file.separator"));
        String[] strings = path.split(splitRegex);
        return Arrays.copyOfRange(strings, Arrays.asList(strings).indexOf("original") + 1, strings.length - 1);
    }
}
