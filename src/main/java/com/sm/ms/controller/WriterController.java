package com.sm.ms.controller;

import com.sm.ms.form.request.CreateNoteForm;
import com.sm.ms.form.response.ResponseMessage;
import com.sm.ms.model.Note;
import com.sm.ms.model.User;
import com.sm.ms.security.jwt.JwtAuthTokenFilter;
import com.sm.ms.security.jwt.JwtProvider;
import com.sm.ms.security.services.UserDetailsServiceImpl;
import com.sm.ms.service.NoteService;
import com.sm.ms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/owner")
public class WriterController {
    @Autowired
    UserService userService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    JwtAuthTokenFilter authenticationJwtTokenFilter;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    NoteService noteService;

    @RequestMapping(value = "/create-note", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<Note> createNote(@RequestBody CreateNoteForm createNoteForm) {
        User user = userService.getUserByAuth();
        Note note = new Note(createNoteForm.getTitle(), createNoteForm.getContent());
        note.setWriter(user);
        noteService.save(note);
        return new ResponseEntity<Note>(note, HttpStatus.OK);
    }


    @RequestMapping(value = "/notes/all", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<List<Note>> listNoteByUser() {
        User user = userService.getUserByAuth();
        List<Note> notes = noteService.findAllByUsername(user.getUsername());
        if (notes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteNote(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserByAuth();
            User writer = noteService.findById(id).getWriter();
            if (user.getId().equals(writer.getId())) {
                noteService.remove(id);
                return new ResponseEntity<>(new ResponseMessage("Delete Note successfully"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseMessage("You are not writer of this note"), HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/note/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<?> editNote(@PathVariable("id") Long id, @RequestBody Note note) {
        try {
            User user = userService.getUserByAuth();
            User writer = noteService.findById(id).getWriter();
            Note expectedNote = noteService.findById(id);
            if (user.getId().equals(writer.getId())) {
                noteService.edit(expectedNote, note);
                noteService.save(expectedNote);
                return new ResponseEntity<>(new ResponseMessage("Update Note successfully"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResponseMessage("You are not writer of this note"), HttpStatus.FORBIDDEN);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
}