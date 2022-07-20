package com.snji.storage.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {
    Optional<BoardTag> findByBoardAndTag(Board board, Tag tag);

    @Query(value =  "select board_id " +
                    "from ( " +
                        "select bt.board_id, count(1) as total " +
                        "from board_tag bt " +
                        "inner join tag t on bt.tag_id = t.tag_id " +
                        "where t.name in (:tags) " +
                        "group by bt.board_id" +
                    ") A " +
                    "where total >= :size", nativeQuery = true)
    List<Long> findByTagNameIn(@Param("tags") List<String> tags, @Param("size") long size);
}