package com.sm.ms.service;

import com.sm.ms.model.Note;

import java.util.List;

public interface NoteService {
    void save(Note note);

    List<Note> findAll();

    Note findById(Long id);

    Note findByTitle(String title);

    void remove(Long id);

}
