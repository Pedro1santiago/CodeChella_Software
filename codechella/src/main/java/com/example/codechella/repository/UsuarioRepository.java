package com.example.codechella.repository;

import com.example.codechella.models.users.Usuario;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, Long> {
    Mono<Usuario> findByEmail(String email);

    @Query("SELECT * FROM usuario WHERE deletado = FALSE OR deletado IS NULL")
    Flux<Usuario> findAllAtivos();

    @Query("SELECT * FROM usuario WHERE deletado = TRUE")
    Flux<Usuario> findAllDeletados();
}
