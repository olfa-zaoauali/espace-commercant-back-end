package com.PFE.Espacecommercant.Authentification.config.Service;

import com.PFE.Espacecommercant.Authentification.config.DTO.AuthenticationRequest;
import com.PFE.Espacecommercant.Authentification.config.DTO.AuthenticationResponse;
import com.PFE.Espacecommercant.Authentification.config.DTO.RegisterRequest;
import com.PFE.Espacecommercant.Authentification.config.Repository.UserRepository;
import com.PFE.Espacecommercant.Authentification.config.user.Role;
import com.PFE.Espacecommercant.Authentification.config.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user= User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken=jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
         authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
         var user = repository.findByEmail(request.getEmail()).orElseThrow();
         var jwtToken=jwtService.generateToken(user);
         return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public Optional<User> findByemail(String email) {
        Optional<User> UserEntity= repository.findByEmail(email);
        return UserEntity;
    }
}
