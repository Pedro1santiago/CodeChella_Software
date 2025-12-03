package com.example.codechella.models.evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoDTO(
        Long id,
        TipoEvento tipo,
        String nome,
        LocalDateTime data,
        String local,
        String descricao,
        BigDecimal preco,
        Integer ingressosDisponiveis,
        StatusEvento statusEvento,
        Long idAdminCriador
) {
    public static EventoDTO toDto(Evento evento) {
        return new EventoDTO(
                evento.getId(),
                evento.getTipo(),
                evento.getNome(),
                evento.getData(),
                evento.getLocal(),
                evento.getDescricao(),
                evento.getPreco(),
                evento.getNumeroIngressosDisponiveis(),
                evento.getStatusEvento(),
                evento.getIdAdminCriador()
        );
    }

    public Evento toEntity() {
        Evento evento = new Evento();
        evento.setId(this.id);
        evento.setTipo(this.tipo);
        evento.setNome(this.nome);
        evento.setData(this.data);
        evento.setLocal(this.local);
        evento.setDescricao(this.descricao);
        evento.setPreco(this.preco);
        evento.setNumeroIngressosDisponiveis(this.ingressosDisponiveis);
        evento.setStatusEvento(this.statusEvento);
        evento.setIdAdminCriador(this.idAdminCriador);
        return evento;
    }
}