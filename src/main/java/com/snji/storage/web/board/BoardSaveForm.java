package com.snji.storage.web.board;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BoardSaveForm {
    @NotEmpty
    private String title;
    private List<String> tags;
}
