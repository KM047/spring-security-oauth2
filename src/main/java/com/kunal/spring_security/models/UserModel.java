package com.kunal.spring_security.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(unique = true, nullable = false)
    private String username;
    @NonNull
    @Column(unique = true)
    private String email;
    @NonNull
    private String password;
    private String role;
    private String provider;
    private String providerId;

}
