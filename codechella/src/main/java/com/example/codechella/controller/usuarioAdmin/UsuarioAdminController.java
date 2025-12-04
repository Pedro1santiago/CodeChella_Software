package com.example.codechella.controller.usuarioAdmin;

import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.ingresso.IngressoDTO;
import com.example.codechella.models.evento.CadastroEventoRequest;
import com.example.codechella.models.users.UsuarioAdminDTO;
import com.example.codechella.serivce.eventoService.EventoService;
import com.example.codechella.serivce.ingressoService.IngressoService;
import com.example.codechella.serivce.usuarioAdmin.UsuarioAdminService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/usuario/admin")
public class UsuarioAdminController {

    private final EventoService eventoService;
    private final UsuarioAdminService usuarioAdminService;
    private final IngressoService ingressoService;

    public UsuarioAdminController(EventoService eventoService, UsuarioAdminService usuarioAdminService, IngressoService ingressoService) {
        this.eventoService = eventoService;
        this.usuarioAdminService = usuarioAdminService;
        this.ingressoService = ingressoService;
    }

    @PostMapping("/cadastrar/evento")
    public Mono<EventoDTO> cadastrar(@RequestBody CadastroEventoRequest request,
                                     @RequestHeader("admin-id") Long adminId) {
        return eventoService.cadastrarEvento(adminId, request.eventoDTO());
    }

    @GetMapping("/eventos")
    public Flux<EventoDTO> listarEventos() {
        return eventoService.listarTodosAtivos();
    }

    @GetMapping("/usuarios")
    public Flux<UsuarioAdminDTO> listarUsuarios() {
        return usuarioAdminService.listarTodos();
    }

    @GetMapping("/ingressos")
    public Flux<IngressoDTO> listarIngressos() {
        return ingressoService.listarTodos();
    }

    @DeleteMapping("/eventos/{id}")
    public Mono<Void> excluirEvento(@PathVariable Long id, @RequestHeader("admin-id") Long adminId) {
        return eventoService.excluir(id, adminId);
    }

    @PutMapping("/ingressos/cancelar/{id}")
    public Mono<IngressoDTO> cancelarIngresso(@PathVariable Long id) {
        return ingressoService.cancelarIngresso(id);
    }
}
