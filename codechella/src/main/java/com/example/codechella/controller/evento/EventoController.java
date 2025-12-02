package com.example.codechella.controller.evento;

import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.users.TipoUsuario;
import com.example.codechella.serivce.eventoService.EventoService;
import com.example.codechella.serivce.superAdminService.SuperAdminService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService service;
    private final SuperAdminService superAdminService;

    public EventoController(EventoService service, SuperAdminService superAdminService) {
        this.service = service;
        this.superAdminService = superAdminService;
    }

    // Lista todos os eventos (JSON normal)
    @GetMapping
    public Flux<EventoDTO> listarTodos() {
        return service.listarTodos();
    }

    // Lista eventos por categoria
    @GetMapping("/categoria/{tipo}")
    public Flux<EventoDTO> obterPorTipo(@PathVariable String tipo) {
        return service.obterPorTipo(tipo);
    }

    // Busca evento por ID
    @GetMapping("/{id}")
    public Mono<EventoDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // Criação de evento — Admin e Super Admin podem
    @PostMapping
    public Mono<EventoDTO> cadastrar(
            @RequestBody EventoDTO dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        // extrai id do token
        Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);

        // verifica se é SUPER ou ADMIN
        return superAdminService.obterTipoDoUsuario(usuarioId)
                .flatMap(tipo -> {
                    if (tipo != TipoUsuario.ADMIN && tipo != TipoUsuario.SUPER) {
                        return Mono.error(new RuntimeException("Apenas administradores podem criar eventos."));
                    }
                    return service.cadastrarEvento(usuarioId, dto);
                });
    }

    // Excluir evento — Admin e Super Admin podem
    @DeleteMapping("/{id}")
    public Mono<Void> excluir(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);

        return superAdminService.obterTipoDoUsuario(usuarioId)
                .flatMap(tipo -> {
                    if (tipo != TipoUsuario.ADMIN && tipo != TipoUsuario.SUPER) {
                        return Mono.error(new RuntimeException("Apenas administradores podem excluir eventos."));
                    }
                    return service.excluir(id, usuarioId);
                });
    }

    // Atualizar evento — Admin e Super Admin podem
    @PutMapping("/{id}")
    public Mono<EventoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody EventoDTO dto,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long usuarioId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);

        return superAdminService.obterTipoDoUsuario(usuarioId)
                .flatMap(tipo -> {
                    if (tipo != TipoUsuario.ADMIN && tipo != TipoUsuario.SUPER) {
                        return Mono.error(new RuntimeException("Apenas administradores podem atualizar eventos."));
                    }
                    return service.atualizarId(id, dto, usuarioId);
                });
    }
}
