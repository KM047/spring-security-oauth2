package com.kunal.spring_security.controllers;

import com.kunal.spring_security.dto.UserDTO;
import com.kunal.spring_security.models.UserModel;
import com.kunal.spring_security.services.UserServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Slf4j
public class HomeController {

    private final UserServices userService;

    public HomeController(UserServices userService) {
        this.userService = userService;
    }

    @GetMapping
    public String home() {
        return "Welcome to the Spring Security Application!";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {

        UserModel user = new UserModel();

        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setEmail(userDTO.getEmail());

        UserModel savedUser = userService.saveUser(user);
        return new ResponseEntity<>(
                savedUser,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO user) {

        try {
            String token = userService.login(user);

            return new ResponseEntity<>(
                    token,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }

    }
}
