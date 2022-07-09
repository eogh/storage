package com.snji.storage.web.board.form;

import com.snji.storage.domain.board.Tag;
import com.snji.storage.domain.file.UploadFile;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BoardSaveForm {
    @NotEmpty
    private String title;
    private List<Tag> tags;
    private List<UploadFile> files;
}
