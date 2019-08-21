package com.sm.ms.service;

import com.sm.ms.model.Note;

public interface NoteService {
    Note save(Note note);

    Note findById(Long id);

    Note findByTitle(String title);

}
