package com.example.codechella.models.users;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario,
        LocalDateTime criadoEm,
        String token 
) {}
