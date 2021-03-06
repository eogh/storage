package com.snji.storage.web.board;

import com.snji.storage.domain.board.*;
import com.snji.storage.domain.file.UploadFile;
import com.snji.storage.domain.file.UploadFileRepository;
import com.snji.storage.web.board.form.BoardSaveForm;
import com.snji.storage.web.board.form.BoardSearchCond;
import com.snji.storage.web.board.form.BoardTagForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final BoardFileRepository boardFileRepository;
    private final UploadFileRepository uploadFileRepository;

    @GetMapping
    public String boards(Model model) {
        model.addAttribute("tags", tagRepository.findAll());
        return "boards/boards";
    }

    @GetMapping("/{boardId}")
    public String board(@PathVariable long boardId, Model model) {
        model.addAttribute("board", boardRepository.getById(boardId));
        model.addAttribute("tags", tagRepository.findAll());
        return "boards/board";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute BoardSaveForm form, Model model) {
        model.addAttribute("tags", tagRepository.findAll());
        return "boards/addForm";
    }

    @PostMapping("/api/add")
    @ResponseBody
    public Board addBoard(@RequestBody @Valid BoardSaveForm form, BindingResult bindingResult) {

        Board addBoard = boardRepository.save(
                Board.builder()
                        .title(form.getTitle())
                        .build());

        for (Tag tag : form.getTags()) {
            boardTagRepository.save(
                    BoardTag.builder()
                            .board(addBoard)
                            .tag(tag)
                            .build());
        }

        for (UploadFile file : form.getFiles()) {
            boardFileRepository.save(
                    BoardFile.builder()
                            .board(addBoard)
                            .file(file)
                            .build());
        }

        return addBoard;
    }

    @GetMapping("/{boardId}/edit")
    public String editForm(@PathVariable Long boardId, Model model) {
        model.addAttribute("board", boardRepository.findById(boardId).orElse(null));
        model.addAttribute("tags", tagRepository.findAll());
        return "boards/editForm";
    }

    @GetMapping("/{boardId}/delete")
    public String delete(@PathVariable Long boardId) {
        Board board = boardRepository.getById(boardId);

        for (BoardTag boardTag : board.getBoardTags()) {
            boardTagRepository.delete(boardTag);
        }
        for (BoardFile boardFile : board.getBoardFiles()) {
            boardFileRepository.delete(boardFile);
        }
        boardRepository.delete(board);

        return "redirect:/boards";
    }

    @GetMapping("/api/list")
    @ResponseBody
    public Page<Board> findBoards(BoardSearchCond condition, Pageable pageable) {
        log.info("condition.getTags() : {}", condition.getTags());

        if (condition.getTags() != null) {
            List<Long> boardIds = boardTagRepository.findByTagNameIn(condition.getTags(), condition.getTags().size());
            return boardRepository.findByIdIn(boardIds, pageable);
        }
        return boardRepository.findAll(pageable);
    }

    @PostMapping("/api/{boardId}/tags/add")
    @ResponseBody
    public String boardTagsAdd(@PathVariable Long boardId,
                               @RequestBody @Validated BoardTagForm form, BindingResult bindingResult) {

        Board board = boardRepository.findById(boardId).orElse(null);
        Tag tag = tagService.add(form.getTagName());

        if (board != null) {
            if (!boardTagRepository.findByBoardAndTag(board, tag).isPresent()) {
                boardTagRepository.save(BoardTag.builder()
                        .board(board)
                        .tag(tag)
                        .build());
            }
        } else {
            return "fail";
        }

        return "success";
    }
}
