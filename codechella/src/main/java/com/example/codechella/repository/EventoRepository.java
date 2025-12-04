package com.example.codechella.repository;

import com.example.codechella.models.evento.Evento;
import com.example.codechella.models.evento.TipoEvento;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface EventoRepository extends ReactiveCrudRepository<Evento, Long> {

    Flux<Evento> findByTipo(TipoEvento tipo);

    @Query("SELECT * FROM eventos WHERE cancelado = FALSE OR cancelado IS NULL ORDER BY data DESC")
    Flux<Evento> findAllAtivos();

    @Query("SELECT * FROM eventos WHERE id_admin_criador = :idUsuario AND cancelado = TRUE")
    Flux<Evento> findEventosCanceladosPorUsuario(Long idUsuario);
}
