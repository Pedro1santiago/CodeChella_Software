package com.example.codechella.models.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("usuario")
public class Usuario {

    @Id
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipoUsuario;

    @Column("created_at")
    private LocalDateTime criadoEm;

    @Column("deletado")
    private Boolean deletado = false;

    @Column("data_exclusao")
    private LocalDateTime dataExclusao;

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senha, TipoUsuario tipoUsuario, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getNome() { return nome; }
    public void setNome(String nome){ this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email){ this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha){ this.senha = senha; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario){
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm){ this.criadoEm = criadoEm; }

    public Boolean getDeletado() {
        return deletado;
    }

    public void setDeletado(Boolean deletado) {
        this.deletado = deletado;
    }

    public LocalDateTime getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(LocalDateTime dataExclusao) {
        this.dataExclusao = dataExclusao;
    }
}
