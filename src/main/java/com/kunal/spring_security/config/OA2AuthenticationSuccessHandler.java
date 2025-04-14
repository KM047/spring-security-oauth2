package com.kunal.spring_security.config;

import com.kunal.spring_security.dto.UserDTO;
import com.kunal.spring_security.models.UserModel;
import com.kunal.spring_security.repository.UserRepository;
import com.kunal.spring_security.services.JWTService;
import com.kunal.spring_security.services.UserServices;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
@Slf4j
public class OA2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServices userService;

    @Autowired
    private JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("OA2AuthenticationSuccessHandler started ------------------------------------------");

        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();


        user.getAttributes().forEach((key, value) -> log.info("{}: {}", key, value));

        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

        UserModel newUser = new UserModel();

        newUser.setRole("USER");

        newUser.setPassword(UUID.randomUUID().toString());

        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            newUser.setUsername(user.getAttribute("email").toString().split("@")[0]);
            newUser.setEmail(user.getAttribute("email").toString());

            newUser.setProvider("GOOGLE");
            newUser.setProviderId(user.getName());
        }
//        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
//
//            newUser.setEmail(
//                    user.getAttribute("email") != null ? user.getAttribute("email")
//                            : user.getAttribute("login").toString() + "@gmail.com");
//
//            newUser.setUsername(user.getAttribute("login").toString());
//            newUser.setProvider("GITHUB");
//            newUser.setProviderId(user.getAttribute(user.getName()));
//
//        }

        UserModel availableUser = userRepository.findByEmail(newUser.getEmail());

        if (availableUser == null) {
            availableUser = userService.saveUser(newUser);
        } else {
            userService.updateUser(availableUser);
        }


        UserDTO userDTO = new UserDTO();

        userDTO.setId(availableUser.getId());
        userDTO.setUsername(availableUser.getUsername());
        userDTO.setRole(availableUser.getRole());


        String jwt = jwtService.generateToken(userDTO);

        // 4. Send token in response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"jwt\": \"" + jwt + "\"}");
    }
}
