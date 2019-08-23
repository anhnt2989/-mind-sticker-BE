package com.sm.ms.repository;

import com.sm.ms.model.Note;
import com.sm.ms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Boolean existsByTitle(String title);

    Note findByTitle(String title);

    List<Note> findAllByWriter(User user);
}
