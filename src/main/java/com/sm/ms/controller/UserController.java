package com.sm.ms.controller;

import com.sm.ms.form.response.JwtResponse;
import com.sm.ms.form.response.ResponseMessage;
import com.sm.ms.model.User;
import com.sm.ms.security.jwt.JwtProvider;
import com.sm.ms.security.services.UserPrinciple;
import com.sm.ms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProvider jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserPrinciple getCurrentUser() {
        return (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<List<User>> listAllUser() {
        List<User> users = this.userService.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<List<User>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/updateCurrent", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User currentUser = userService.findById(getCurrentUser().getId());

        currentUser.setEmail(user.getEmail());
        currentUser.setUsername(user.getUsername());
        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
        currentUser.setLastName(user.getLastName());
        currentUser.setFirstName(user.getFirstName());
        userService.save(currentUser);

        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/Current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<User> getUserById() {
        User user = userService.findById(getCurrentUser().getId());
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/confirmPassword", method = RequestMethod.POST)
    @PreAuthorize("hasRole('GUEST') or hasRole('HOST') or hasRole('ADMIN') or hasRole('PM')")
    public ResponseEntity<ResponseMessage> comparePassword(@RequestBody String password) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(getCurrentUser().getUsername(), password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenUtil.generateJwtToken(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return new  ResponseEntity<ResponseMessage>(
                    new ResponseMessage("confirm Succssess"), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<ResponseMessage>(new ResponseMessage( "confirm fail"), HttpStatus.NOT_FOUND);
        }
    }
}
