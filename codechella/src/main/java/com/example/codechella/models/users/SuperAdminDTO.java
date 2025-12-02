package com.example.codechella.models.users;

public record SuperAdminDTO(
        Long id,
        String nome,
        String email,
        String senha,
        TipoUsuario tipoUsuario,
        String token // novo campo
) {
    public static SuperAdminDTO toDTO(SuperAdmin superAdmin, String token) {
        return new SuperAdminDTO(
                superAdmin.getIdSuperAdmin(),
                superAdmin.getNome(),
                superAdmin.getEmail(),
                superAdmin.getSenha(),
                superAdmin.getTipoUsuario(),
                token
        );
    }
}
