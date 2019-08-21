package com.sm.ms.controller;

import com.sm.ms.form.request.CreateNoteForm;
import com.sm.ms.form.response.ResponseMessage;
import com.sm.ms.model.Note;
import com.sm.ms.model.User;
import com.sm.ms.security.jwt.JwtAuthTokenFilter;
import com.sm.ms.security.jwt.JwtProvider;
import com.sm.ms.security.services.UserDetailsServiceImpl;
import com.sm.ms.service.UserService;
import com.sm.ms.service.impl.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class NoteController {
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
    NoteServiceImpl noteService;

    //Tạo note mới với chỉ quyền user - PM - ADMIN
    @PostMapping(value = "create-note", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<?> createNote(@ModelAttribute CreateNoteForm createNoteForm, HttpServletRequest request) {
        String jwts = authenticationJwtTokenFilter.getJwt(request);
        String userName = jwtProvider.getUserNameFromJwtToken(jwts);
        User user;
        try {
            user = userService.findByUsername(userName).orElseThrow(
                    () -> new UsernameNotFoundException("User Not Found with -> username or email : " + userName));
        } catch (UsernameNotFoundException exception) {
            return new ResponseEntity<>(new ResponseMessage(exception.getMessage()), HttpStatus.NOT_FOUND);
        }

        Note note = new Note(createNoteForm.getTitle(), createNoteForm.getContent());
        note.setWriter(user);
        noteService.save(note);
        Note noteTitle = noteService.findByTitle(createNoteForm.getTitle());
        return new ResponseEntity<>(new ResponseMessage("Note created successfully"), HttpStatus.OK);
    }

    //Hiển thị all notes (phải cần login mới biết note nào của user nào)
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<List<Note>> listAllNotes() {
        List<Note> notes = noteService.findAll();
        if (notes.isEmpty()) {
            return new ResponseEntity<List<Note>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Note>>(notes, HttpStatus.OK);
    }

    //Sửa 1 note với id
    @RequestMapping(value = "/notes/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('USER') or hasRole('PM') or hasRole('ADMIN')")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note note) {
        System.out.println("Updating Note " + id);

        Note currentNote = noteService.findById(id);

        if (currentNote == null) {
            System.out.println("Note with id " + id + " not found");
            return new ResponseEntity<Note>(HttpStatus.NOT_FOUND);
        }

        currentNote.setTitle(note.getTitle());
        currentNote.setContent(note.getContent());

        noteService.save(currentNote);
        return new ResponseEntity<Note>(currentNote, HttpStatus.OK);
    }
}
