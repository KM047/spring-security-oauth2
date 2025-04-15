package com.kunal.spring_security.services;

import com.kunal.spring_security.dto.UserDTO;
import com.kunal.spring_security.models.UserModel;
import com.kunal.spring_security.repository.UserRepository;
import com.kunal.spring_security.utils.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServices {


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JWTService jwtService;

    public UserModel saveUser(UserModel user) {

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            if (user.getProvider() == null) {
                user.setProvider("LOCAL");
            }
            user.setRole("USER");

            UserModel savedUser = userRepository.save(user);

            if (savedUser != null) {
                log.info("User is created successfully for {} : ", user.getUsername());
                return savedUser;
            }
        } catch (Exception e) {

            log.error("Error while creating user for {} : ", user.getUsername(), e);

            throw new RuntimeException(e);
        }

        return null;

    }

    public Optional<UserModel> updateUser(UserModel user) {

        try {
            UserModel availableUser = null;

            if (user.getId() != null) {

                availableUser = userRepository.findById(user.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            } else {
                availableUser = userRepository.findByEmail(user.getEmail());
            }

            if (availableUser == null) {
                throw new ResourceNotFoundException("User not found");
            }

            // update user with available user
            availableUser.setUsername(user.getEmail().split("@")[0]);
            availableUser.setEmail(user.getEmail());
            availableUser.setPassword(user.getPassword());

            UserModel updatedUser = userRepository.save(availableUser);

            return Optional.ofNullable(updatedUser);
        } catch (Exception e) {

            log.error("Error while updating user for {} : ", user.getUsername(), e);
            throw new RuntimeException(e);
        }

    }

    public ResponseEntity<?> login(UserDTO user) {
        try {
            UserModel userInDB = userRepository.findByUsername(user.getUsername());

            if (!userInDB.getProvider().equalsIgnoreCase("LOCAL")) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message", "This account is registered with " + userInDB.getProvider().toLowerCase() +
                                        ". Please login using " + userInDB.getProvider().toLowerCase() + "."
                        ));

            } else {

                Authentication authenticate = manager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));


                if (authenticate.isAuthenticated()) {

                    UserDTO userDTO = new UserDTO();

                    userDTO.setId(userInDB.getId());
                    userDTO.setUsername(userInDB.getUsername());
                    userDTO.setRole(userInDB.getRole());

                    String jwtToken = jwtService.generateToken(userDTO);
                    return ResponseEntity.ok(Map.of("token", jwtToken));


                }
            }

        } catch (Exception e) {
            log.error("Error while logging in user with email {} : ", user.getEmail(), e);
            throw new RuntimeException("Error while logging in", e);
        }

        return null;
    }
}
