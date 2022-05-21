package com.snji.storage.web.board;

import com.snji.storage.domain.board.BoardRepository;
import com.snji.storage.domain.board.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "boards/board";
    }

    @GetMapping("/api/list")
    public String findBoards(Model model, BoardSearchCond condition) {
        log.info("condition.getTags() : {}", condition.getTags());

        if (condition.getTags() == null) {
            model.addAttribute("boards", boardRepository.findAll());
        } else {
            model.addAttribute("boards", boardRepository.search(condition.getTags(), condition.getTags().size()));
        }
        model.addAttribute("searchTags", condition.getTags());

        return "/boards/boards :: #resultDiv";
    }
}
