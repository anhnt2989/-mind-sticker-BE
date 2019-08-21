package com.sm.ms.service.impl;

import com.sm.ms.model.Note;
import com.sm.ms.repository.NoteRepository;
import com.sm.ms.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    NoteRepository noteRepository;

    @Override
    public Note save(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public Note findById(Long id) {
        return noteRepository.findById(id).get();
    }

    @Override
    public Note findByTitle(String title) {
        return noteRepository.findByTitle(title);
    }
}
