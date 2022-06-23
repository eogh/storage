package com.snji.storage.web.board;

import com.snji.storage.domain.board.*;
import com.snji.storage.web.board.form.BoardSaveForm;
import com.snji.storage.web.board.form.BoardSearchCond;
import com.snji.storage.web.board.form.BoardTagForm;
import com.snji.storage.web.board.form.BoardUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;
    private final TagRepository tagRepository;
    private final BoardFileRepository boardFileRepository;
    private final FileRepository fileRepository;

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

        for (File file : form.getFiles()) {
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
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            return "";
        }

        model.addAttribute("board", board);
        return "boards/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long boardId, @Validated @ModelAttribute BoardUpdateForm form, BindingResult bindingResult) {
        return "";
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
    public String findBoards(Model model, BoardSearchCond condition) {
        log.info("condition.getTags() : {}", condition.getTags());

        List<Board> list;
        if (condition.getTags() == null) {
            list = boardRepository.findAll();
        } else {
            list = boardRepository.search(condition.getTags(), condition.getTags().size());
        }
        model.addAttribute("boards", list);
        model.addAttribute("size", list.size());

        return "/boards/boards :: #resultDiv";
    }

    @PostMapping("/api/{boardId}/tags/add")
    @ResponseBody
    public String boardTagsAdd(@PathVariable Long boardId,
                               @RequestBody @Validated BoardTagForm form, BindingResult bindingResult) {

        Board board = boardRepository.findById(boardId).orElse(null);

        Tag tag;
        Optional<Tag> findTag = tagRepository.findByName(form.getTagName()).stream().findFirst();
        if (findTag.isEmpty()) {
            tag = tagRepository.save(Tag.builder().name(form.getTagName()).build());
        } else {
            tag = findTag.get();
        }

        if (board != null) {
            if (boardTagRepository.findByBoardAndTag(board, tag).isEmpty()) {
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
