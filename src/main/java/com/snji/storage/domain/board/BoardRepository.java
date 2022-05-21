package com.snji.storage.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("select distinct b from Board b " +
            "join fetch b.boardTags bt " +
            "join fetch bt.tag t " +
            "where (" +
                "select count(1) from BoardTag sbt " +
                "inner join sbt.tag st " +
                "where st.name in (:tags) and b.id = sbt.board.id"+
            ") >= :size")
    List<Board> search(@Param("tags") List<String> tags, @Param("size") long size);
}