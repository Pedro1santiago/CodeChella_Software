package com.example.codechella.controller.permissao;

import com.example.codechella.models.users.SolicitacaoPermissaoDTO;
import com.example.codechella.serivce.permissao.PermissaoService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/permissoes")
public class PermissaoController {

    private final PermissaoService permissaoService;

    public PermissaoController(PermissaoService permissaoService) {
        this.permissaoService = permissaoService;
    }

    @PostMapping("/solicitar")
    public Mono<SolicitacaoPermissaoDTO> solicitarPermissaoAdmin(@RequestHeader("usuario-id") Long idUsuario) {
        return permissaoService.solicitarPermissaoAdmin(idUsuario);
    }

    @GetMapping("/minhas-solicitacoes")
    public Flux<SolicitacaoPermissaoDTO> minhasSolicitacoes(@RequestHeader("usuario-id") Long idUsuario) {
        return permissaoService.minhasSolicitacoes(idUsuario);
    }

    @GetMapping("/pendentes")
    public Flux<SolicitacaoPermissaoDTO> listarSolicitacoesPendentes(
            @RequestParam(value = "token", required = false) String tokenQuery,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        String token = null;

        if (tokenQuery != null && !tokenQuery.isEmpty()) {
            token = tokenQuery;
            System.out.println("🔵 Token recebido via query string: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("🔵 Token recebido via Authorization header: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        } else {
            System.out.println("⚠️ Nenhum token recebido na requisição");
            throw new RuntimeException("Token JWT é necessário");
        }

        try {
            Long superAdminId = permissaoService.getSuperAdminIdFromToken(token);
            System.out.println("SuperAdminId extraído do token: " + superAdminId);
            return permissaoService.listarSolicitacoesPendentes(superAdminId);
        } catch (Exception e) {
            System.out.println("Erro ao validar token ou extrair SuperAdminId: " + e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}/aprovar")
    public Mono<SolicitacaoPermissaoDTO> aprovarSolicitacao(
            @PathVariable Long id,
            @RequestHeader("super-admin-id") Long superAdminId) {
        return permissaoService.aprovarSolicitacao(id, superAdminId);
    }

    @PutMapping("/{id}/negar")
    public Mono<SolicitacaoPermissaoDTO> negarSolicitacao(
            @PathVariable Long id,
            @RequestHeader("super-admin-id") Long superAdminId,
            @RequestParam(required = false, defaultValue = "") String motivo) {
        return permissaoService.negarSolicitacao(id, motivo, superAdminId);
    }
}
