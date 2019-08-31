package com.sm.ms.service;

import com.sm.ms.model.Note;
import com.sm.ms.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface NoteService {
    void save(Note note);

    void edit(Note currentNote, Note finallyNote);

    List<Note> findAll();

    Note findById(Long id) throws EntityNotFoundException;

    Note findByTitle(String title);

    void remove(Long id);

    List<Note> findAllByUser(User user);

}
