package com.kunal.spring_security.services;


import com.kunal.spring_security.models.UserModel;
import com.kunal.spring_security.repository.UserRepository;
import com.kunal.spring_security.utils.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    //    @Autowired
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel user = userRepository.findByUsername(username);

        if (user != null) {
            return new CustomUserDetails(user.getUsername(), user.getPassword(), getAuthorities(user), user.getId());
        }

        throw new UsernameNotFoundException("User not found with username : " + username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserModel user) {
        return List.of(() -> "ROLE_" + user.getRole());
    }
}
