package com.example.codechella.models.users;

import java.time.LocalDateTime;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRegisterRequest req) {
        Usuario usuario = new Usuario();
        usuario.setNome(req.nome());
        usuario.setEmail(req.email());
        usuario.setSenha(req.senha());
        usuario.setTipoUsuario(TipoUsuario.USER);
        usuario.setCriadoEm(LocalDateTime.now());
        return usuario;
    }

    public static UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getCriadoEm(),
                null // sem token
        );
    }

    public static UsuarioResponseDTO toDTO(Usuario usuario, String token) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getCriadoEm(),
                token
        );
    }
}
