package com.sm.ms.repository;

import com.sm.ms.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Boolean existsByTitle(String title);

    Note findByTitle(String title);
}
