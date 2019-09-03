package com.sm.ms.service;

import com.sm.ms.model.Note;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface NoteService {
    void save(Note note);

    void edit(Note currentNote, Note finalNote);

    List<Note> findAll();

    Note findById(Long id) throws EntityNotFoundException;

    Note findByTitle(String title);

    void remove(Long id);

    List<Note> findAllByUsername(String username);

}
