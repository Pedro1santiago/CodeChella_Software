package com.example.codechella.models.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("super_admin")
public class SuperAdmin {

    @Id
    @Column("id")
    private Long idSuperAdmin;

    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipoUsuario;

    public SuperAdmin() {}

    public SuperAdmin(Long idSuperAdmin, String nome, String email, String senha, TipoUsuario tipoUsuario) {
        this.idSuperAdmin = idSuperAdmin;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    public Long getIdSuperAdmin() {
        return idSuperAdmin;
    }

    public void setIdSuperAdmin(Long idSuperAdmin) {
        this.idSuperAdmin = idSuperAdmin;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public boolean isValid() {
        return tipoUsuario == TipoUsuario.SUPER && email != null && senha != null;
    }
}
