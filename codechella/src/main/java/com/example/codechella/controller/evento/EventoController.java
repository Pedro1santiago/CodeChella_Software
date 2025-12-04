package com.example.codechella.controller.evento;

import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.evento.TipoEvento;
import com.example.codechella.serivce.eventoService.EventoService;
import com.example.codechella.serivce.superAdminService.SuperAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private static final Logger log = LoggerFactory.getLogger(EventoController.class);

    private final EventoService service;
    private final SuperAdminService superAdminService;

    public EventoController(EventoService service, SuperAdminService superAdminService) {
        this.service = service;
        this.superAdminService = superAdminService;
    }

    @GetMapping
    public Flux<EventoDTO> listarTodos() {
        return service.listarTodosAtivos();
    }

    @GetMapping("/categoria/{tipo}")
    public Flux<EventoDTO> obterPorTipo(@PathVariable TipoEvento tipo) {
        return service.obterPorTipo(tipo)
                .switchIfEmpty(Flux.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum evento encontrado para a categoria: " + tipo)
                ));
    }

    @GetMapping("/{id}")
    public Mono<EventoDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Mono<EventoDTO> cadastrar(
            @RequestBody EventoDTO dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return service.cadastrarEvento(usuarioId, dto);
    }

    @PatchMapping("/{id}/cancelar")
    public Mono<EventoDTO> cancelar(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        log.info("[CANCELAR-CONTROLLER] Iniciando - eventoId: {}", id);

        try {
            Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
            log.info("[CANCELAR-CONTROLLER] UsuarioId extraído: {}", usuarioId);

            return service.cancelarEvento(id, usuarioId)
                    .doOnSuccess(dto -> log.info("[CANCELAR-CONTROLLER] Sucesso - eventoId: {}", id))
                    .doOnError(e -> log.error("[CANCELAR-CONTROLLER] Erro: {}", e.getMessage(), e));
        } catch (Exception e) {
            log.error("[CANCELAR-CONTROLLER] Exceção ao extrair token: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    @DeleteMapping("/{id}")
    public Mono<Void> excluir(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return service.excluir(id, usuarioId);
    }

    @PutMapping("/{id}")
    public Mono<EventoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody EventoDTO dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return service.atualizarId(id, dto, usuarioId);
    }
}
