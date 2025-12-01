package com.example.codechella.controller.evento;

import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.serivce.eventoService.EventoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
@RequestMapping("/eventos")
public class EventoControler {

    private final EventoService service;
    private final Sinks.Many<EventoDTO> eventoSink;

    public EventoControler(EventoService service) {
        this.service = service;
        this.eventoSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EventoDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping(value = "/categoria/{tipo}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EventoDTO> obterPorTipo(@PathVariable String tipo) {

        return service.obterPorTipo(tipo);
    }

    @GetMapping("/{id}")
    public Mono<EventoDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Mono<EventoDTO> cadastrar(
            @RequestBody EventoDTO dto,
            @RequestHeader("admin-id") Long adminId
    ) {
        return service.cadastrarEvento(adminId, dto)
                .doOnSuccess(eventoSink::tryEmitNext);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> excluir(
            @PathVariable Long id,
            @RequestHeader("admin-id") Long adminId
    ) {
        return service.excluir(id, adminId);
    }

    @PutMapping("/{id}")
    public Mono<EventoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody EventoDTO dto,
            @RequestHeader("admin-id") Long adminId
    ) {
        return service.atualizarId(id, dto, adminId);
    }
}
