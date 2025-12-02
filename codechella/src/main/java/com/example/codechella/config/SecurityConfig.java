package com.example.codechella.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String secretKey;

    public SecurityConfig(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter(secretKey);

        ReactiveAuthenticationManager authenticationManager = authentication ->
                Mono.just(authentication);

        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authenticationManager);
        jwtFilter.setServerAuthenticationConverter(converter);
        jwtFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(java.util.List.of(
                            "http://localhost:5173",
                            "https://codechalle-front.vercel.app"
                    ));
                    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(java.util.List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/eventos").permitAll()
                        .pathMatchers(HttpMethod.GET, "/eventos/**").permitAll()
                        .pathMatchers("/permissoes/pendentes").hasRole("SUPER")
                        .pathMatchers("/permissoes/*/aprovar").hasRole("SUPER")
                        .pathMatchers("/permissoes/*/negar").hasRole("SUPER")
                        .anyExchange().authenticated()
                );

        return http.build();
    }

    public String getSecretKey() {
        return secretKey;
    }
}