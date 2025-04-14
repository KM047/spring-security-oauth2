//package com.kunal.spring_security.controllers;
//
//import com.kunal.spring_security.dto.UserDTO;
//import com.kunal.spring_security.services.JWTService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//public class OAuthController {
//
//    @Autowired
//    private JWTService jwtUtil;
//
//    @GetMapping("/oauth2/success")
//    public ResponseEntity<?> oauth2Success(Authentication authentication) {
//        String username = authentication.getName(); // email usually
//        UserDTO user = UserDTO.builder().username(username).build();
//        String jwt = jwtUtil.generateToken(user);
//        return ResponseEntity.ok(
//                Map.of("token", jwt)
//        );
//    }
//}
