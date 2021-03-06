package com.snji.storage.web.board;

import com.snji.storage.domain.board.Tag;
import com.snji.storage.domain.board.TagService;
import com.snji.storage.web.board.form.BoardTagForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @PostMapping("/api/add")
    @ResponseBody
    public Tag add(@RequestBody @Validated BoardTagForm form, BindingResult bindingResult) {
        return tagService.add(form.getTagName());
    }
}
