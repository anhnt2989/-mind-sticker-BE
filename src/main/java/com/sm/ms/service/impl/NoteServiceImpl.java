package com.sm.ms.service.impl;

import com.sm.ms.model.Note;
import com.sm.ms.model.User;
import com.sm.ms.repository.NoteRepository;
import com.sm.ms.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    NoteRepository noteRepository;

    @Override
    public List<Note> findAll() {
        return noteRepository.findAll();
    }

    @Override
    public void remove(Long id) {
        noteRepository.deleteById(id);
    }

    @Override
    public void save(Note note) {
        noteRepository.save(note);
    }

    @Override
    public Note findById(Long id) throws EntityNotFoundException {
        return noteRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

//    @Override
//    public List<Note> findAllByUser(User user) {
//        return noteRepository.findAllByWriter(user);
//    }

    @Override
    public Note findByTitle(String title) {
        return noteRepository.findByTitle(title);
    }

    @Override
    public void edit(Note currentNote, Note finalNote) {
        currentNote.setTitle(finalNote.getTitle());
        currentNote.setContent(finalNote.getContent());
    }
}
