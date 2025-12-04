package com.example.codechella.models.evento;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("eventos")
public class Evento {

    @Id
    private Long id;
    private TipoEvento tipo;
    private String nome;
    private LocalDateTime data;
    private String local;
    private String descricao;
    private BigDecimal preco;

    @Column("numero_ingressos_disponiveis")
    private Integer numeroIngressosDisponiveis;

    @Column("evento_status")
    private StatusEvento statusEvento;

    @Column("id_admin_criador")
    private Long idAdminCriador;

    @Column("cancelado")
    private Boolean cancelado = false;

    // Getters
    public Long getId() {
        return id;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getLocal() {
        return local;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public Integer getNumeroIngressosDisponiveis() {
        return numeroIngressosDisponiveis;
    }

    public StatusEvento getStatusEvento() {
        return statusEvento;
    }

    public Long getIdAdminCriador() {
        return idAdminCriador;
    }

    public Boolean getCancelado() {
        return cancelado;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public void setNumeroIngressosDisponiveis(Integer numeroIngressosDisponiveis) {
        this.numeroIngressosDisponiveis = numeroIngressosDisponiveis;
    }

    public void setStatusEvento(StatusEvento statusEvento) {
        this.statusEvento = statusEvento;
    }

    public void setIdAdminCriador(Long idAdminCriador) {
        this.idAdminCriador = idAdminCriador;
    }

    public void setCancelado(Boolean cancelado) {
        this.cancelado = cancelado;
    }
}
