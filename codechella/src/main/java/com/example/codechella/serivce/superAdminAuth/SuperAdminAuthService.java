package com.example.codechella.serivce.superAdminAuth;

import com.example.codechella.models.users.*;
import com.example.codechella.repository.SuperAdminRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
public class SuperAdminAuthService {

    private final SuperAdminRepository superAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final String secretKey;

    public SuperAdminAuthService(SuperAdminRepository superAdminRepository, PasswordEncoder passwordEncoder,
                                 @Value("${jwt.secret}") String secretKey) {
        this.superAdminRepository = superAdminRepository;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = secretKey;
    }

    public Mono<SuperAdminDTO> login(SuperAdminLoginRequest loginRequest) {
        return superAdminRepository.findByEmail(loginRequest.email())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Super Admin não encontrado")))
                .flatMap(superAdmin -> {
                    if (!passwordEncoder.matches(loginRequest.senha(), superAdmin.getSenha())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta"));
                    }

                    String token = Jwts.builder()
                            .setSubject(superAdmin.getIdSuperAdmin().toString())
                            .claim("roles", List.of(superAdmin.getTipoUsuario().name()))
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                            .compact();

                    return Mono.just(SuperAdminDTO.toDTO(superAdmin, token));
                });
    }
}
