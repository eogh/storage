package com.snji.storage.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {
    Optional<BoardTag> findByBoardAndTag(Board board, Tag tag);
}
