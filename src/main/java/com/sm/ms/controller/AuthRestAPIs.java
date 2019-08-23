package com.sm.ms.controller;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import com.sm.ms.form.request.LoginForm;
import com.sm.ms.form.request.SignUpForm;
import com.sm.ms.form.response.JwtResponse;
import com.sm.ms.form.response.ResponseMessage;
import com.sm.ms.model.ConfirmationToken;
import com.sm.ms.model.Role;
import com.sm.ms.model.RoleName;
import com.sm.ms.model.User;
import com.sm.ms.repository.ConfirmationTokenRepository;
import com.sm.ms.security.jwt.JwtProvider;
import com.sm.ms.security.services.UserPrinciple;
import com.sm.ms.service.RoleService;
import com.sm.ms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserPrinciple userPrinciple = (UserPrinciple) userDetails;
        String avatarLink = userPrinciple.getAvatarFileName() != null ? "resources/images/" + userDetails.getUsername() + "/avatar/" + userPrinciple.getAvatarFileName() : "";
        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities(), avatarLink);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
        System.out.println(signUpRequest);
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userService.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Phone number is already in use"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account

        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                signUpRequest.getBirthday(), signUpRequest.getAddress(),
                signUpRequest.getPhoneNumber(), passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEnabled(false);
//        String avatarFileName = signUpRequest.getAvatar().getOriginalFilename();
//        user.setAvatarFileName(avatarFileName);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
        roles.add(userRole);
        user.setRoles(roles);

//        String saveLocation = UPLOAD_LOCATION + user.getUsername() + "/avatar/";
//        new File(saveLocation).mkdirs();
//        multipartFileService.saveMultipartFile(saveLocation, signUpRequest.getAvatar(), avatarFileName);
//
        userService.save(user);
//        emailSenderService.sendEmailCreateUser(user);

        return new ResponseEntity<>(new ResponseMessage("Please login your email to confirm"), HttpStatus.OK);
    }

    @GetMapping(value = "confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userService.findByEmailIgnoreCase(token.getUser().getEmail());
            user.setEnabled(true);
            userService.save(user);
            return new ResponseEntity<>(new ResponseMessage("User registered successfully!"),
                    HttpStatus.OK);

        } else {
            return new ResponseEntity<>(new ResponseMessage("Fail -> User's register occurred errors!"),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
