package com.snji.storage.web.board;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class BoardUpdateForm {
    @NotEmpty
    private String title;
}
