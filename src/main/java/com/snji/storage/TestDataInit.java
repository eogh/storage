package com.snji.storage;

import com.snji.storage.domain.board.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final BoardRepository boardRepository;
    private final BoardTagRepository boardTagRepository;
    private final TagRepository tagRepository;

//    @PostConstruct
    public void init() {

        List<Tag> tags = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Tag addTag = Tag.builder().name("tag"+i).build();
            tags.add(tagRepository.save(addTag));
        }

        for (int i = 0; i < 10; i++) {
            Board addBoard = boardRepository.save(Board.builder().title("board"+i).build());

            for (Tag tag : tags) {
                if ((int) (Math.random() * 10) > 5) {
                    BoardTag addBoardTag = BoardTag.builder().board(addBoard).tag(tag).build();
                    boardTagRepository.save(addBoardTag);
                }
            }
        }


    }
}
