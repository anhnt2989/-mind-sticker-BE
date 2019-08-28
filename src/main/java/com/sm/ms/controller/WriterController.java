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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "create-note", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<?> createNote(@ModelAttribute CreateNoteForm createNoteForm, HttpServletRequest request) {
        User user;
//        String jwts = authenticationJwtTokenFilter.getJwt(request);
//        String userName = jwtProvider.getUserNameFromJwtToken(jwts);
        try {
            user = userService.getUserByAuth();
        } catch (UsernameNotFoundException exception) {
            return new ResponseEntity<>(new ResponseMessage(exception.getMessage()), HttpStatus.NOT_FOUND);
        }

        Note note = new Note(createNoteForm.getTitle(), createNoteForm.getContent());
        note.setWriter(user);
        noteService.save(note);
//        Note noteTitle = noteService.findByTitle(createNoteForm.getTitle());
        return new ResponseEntity<>(new ResponseMessage("Note created successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "list-notes", method = RequestMethod.GET)
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<List<Note>> listNotes() {
        List<Note> notes = noteService.findAll();
        if (notes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    @PreAuthorize("hasRole('OWNER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<List<Note>> listNoteByUser() {
        User user = userService.getUserByAuth();
        List<Note> notes = noteService.findAllByUser(user);
        if (notes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
//    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
//    public ResponseEntity<Note> getNote(@PathVariable("id") Long id) {
//        try {
//            Note note = noteService.findById(id);
//            return new ResponseEntity<Note>(note, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
//        }
//    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteNote(@PathVariable("id") Long id) {
        try {
            Note note = noteService.findById(id);
            noteService.remove(id);
            return new ResponseEntity<>(new ResponseMessage("Delete Note successfully"), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<?> editNote(@PathVariable("id") Long id, @RequestBody Note note) {
        try {
            User user = userService.getUserByAuth();
            User owner = noteService.findById(id).getWriter();
            if (user.getId().equals(owner.getId())) {
                note.setWriter(user);
                noteService.save(note);
                return new ResponseEntity<>(new ResponseMessage("Update Note successfully"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ResponseMessage("You are not writer of this note"), HttpStatus.FORBIDDEN);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

//    @GetMapping("/categories")
//    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
//    public ResponseEntity<List<Category>> getCategories() {
//        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
//    }
}