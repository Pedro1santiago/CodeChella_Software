package com.example.codechella.controller.superAdmin;

import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.users.UsuarioAdminDTO;
import com.example.codechella.models.users.UsuarioResponseDTO;
import com.example.codechella.serivce.superAdminService.SuperAdminService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/super-admin")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    //Cria um novo administrador a partir do DTO enviado.
    @PostMapping("/criar/admin")
    public Mono<UsuarioAdminDTO> criarAdmin(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UsuarioAdminDTO adminDTO) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.criarAdmin(superAdminId, adminDTO);
    }

    //Lista todos os administradores do sistema em JSON.
    @GetMapping("/listar/admins")
    public Flux<UsuarioAdminDTO> listarAdmins(@RequestHeader("Authorization") String authHeader) {
        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.listarAdmins(superAdminId);
    }

    // Remove um administrador pelo ID.
    @DeleteMapping("/remover/admin/{id}")
    public Mono<Void> removerAdmin(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.removerAdmin(id, superAdminId);
    }

    // Lista todos os usuários do sistema em JSON.
    @GetMapping("/listar/usuarios")
    public Flux<UsuarioResponseDTO> listarUsuarios(@RequestHeader("Authorization") String authHeader) {
        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.listarUsuarios(superAdminId);
    }

    //Remove um usuário pelo ID.
    @DeleteMapping("/remover/usuario/{id}")
    public Mono<Void> removerUsuario(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.removerUsuario(id, superAdminId);
    }

    // Exclui qualquer evento pelo ID.
    @DeleteMapping("/eventos/{id}")
    public Mono<Void> excluirEventoQualquer(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.excluirEventoQualquer(id, superAdminId);
    }

    //Promove um usuário comum para administrador.
    @PutMapping("/promover/admin/{id}")
    public Mono<UsuarioResponseDTO> promoverParaAdmin(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.promoverParaAdmin(id, superAdminId);
    }

    // Rebaixa um administrador para usuário comum.
    @PutMapping("/rebaixar/user/{id}")
    public Mono<UsuarioResponseDTO> rebaixarParaUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.rebaixarParaUser(id, superAdminId);
    }

    // Lista usuários excluídos (soft delete)
    @GetMapping("/usuarios-excluidos")
    public Flux<UsuarioResponseDTO> listarUsuariosExcluidos(@RequestHeader("Authorization") String authHeader) {
        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.listarUsuariosExcluidos(superAdminId);
    }

    // Lista eventos cancelados de um usuário específico
    @GetMapping("/eventos-cancelados/{idUsuario}")
    public Flux<EventoDTO> listarEventosCancelados(
            @PathVariable Long idUsuario,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.listarEventosCancelados(idUsuario, superAdminId);
    }

    // Reativa um evento cancelado e transfere para o Super Admin
    @PutMapping("/reativar-evento/{idEvento}")
    public Mono<EventoDTO> reativarEvento(
            @PathVariable Long idEvento,
            @RequestHeader("Authorization") String authHeader) {

        Long superAdminId = superAdminService.extrairIdSuperAdminDoHeader(authHeader);
        return superAdminService.reativarEvento(idEvento, superAdminId);
    }
}
