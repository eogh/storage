package com.snji.storage.web.board;

import com.snji.storage.domain.board.Board;
import com.snji.storage.domain.board.BoardRepository;
import com.snji.storage.domain.board.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final TagRepository tagRepository;

    @GetMapping
    public String boards(Model model) {
        model.addAttribute("tags", tagRepository.findAll());
        return "boards/boards";
    }

    @GetMapping("/{boardId}")
    public String board(@PathVariable long boardId, Model model) {
        model.addAttribute("board", boardRepository.getById(boardId));
        return "boards/board";
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute BoardSaveForm form) {
        return "boards/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute BoardSaveForm form, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "boards/addForm";
        }

        Board board = Board.builder()
                .title(form.getTitle())
                .build();

        Board savedBoard = boardRepository.save(board);

        redirectAttributes.addAttribute("boardId", savedBoard.getId());
        return "redirect:/boards/{boardId}";
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
}
