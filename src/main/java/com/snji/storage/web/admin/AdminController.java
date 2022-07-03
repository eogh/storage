package com.snji.storage.web.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String admin() {
        return "admin/admin";
    }

    /**
     *
     * @return
     */
    @GetMapping("/api/board-sync")
    public String boardSync() {

        return "success";
    }
}
