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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

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
        return new ResponseEntity<>(new ResponseMessage("Publish House successfully"), HttpStatus.OK);
    }
}
