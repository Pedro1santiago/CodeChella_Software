package com.example.codechella.serivce.permissao;

import com.example.codechella.models.users.*;
import com.example.codechella.repository.SolicitacaoPermissaoRepository;
import com.example.codechella.repository.UsuarioRepository;
import com.example.codechella.repository.SuperAdminRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PermissaoService {

    @Autowired
    private SolicitacaoPermissaoRepository solicitacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Mono<Void> validarSuperAdminPorId(Long superAdminId) {
        return superAdminRepository.findById(superAdminId)
                .filter(s -> s.getTipoUsuario() == TipoUsuario.SUPER)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN)))
                .then();
    }

    public Long getSuperAdminIdFromToken(String token) {
        try {
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            if (subject == null || subject.isEmpty()) {
                throw new IllegalArgumentException("Token sem subject");
            }

            return Long.parseLong(subject);

        } catch (Exception e) {
            System.out.println("Erro ao validar token: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
    }

    public Mono<SolicitacaoPermissaoDTO> solicitarPermissaoAdmin(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(usuario -> {
                    if (usuario.getTipoUsuario() != TipoUsuario.USER) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
                    }
                    SolicitacaoPermissao solicitacao = new SolicitacaoPermissao(idUsuario, TipoPermissao.ADMIN);
                    return solicitacaoRepository.save(solicitacao);
                })
                .flatMap(solicitacao -> usuarioRepository.findById(idUsuario)
                        .map(usuario -> SolicitacaoPermissaoDTO.fromEntity(solicitacao, usuario.getNome())));
    }

    public Flux<SolicitacaoPermissaoDTO> listarSolicitacoesPendentes(Long superAdminId) {
        return validarSuperAdminPorId(superAdminId)
                .thenMany(solicitacaoRepository.findByStatus(StatusPermissao.PENDENTE)
                        .flatMap(solicitacao -> usuarioRepository.findById(solicitacao.getIdUsuario())
                                .map(usuario -> SolicitacaoPermissaoDTO.fromEntity(solicitacao, usuario.getNome()))));
    }

    public Mono<SolicitacaoPermissaoDTO> aprovarSolicitacao(Long idSolicitacao, Long superAdminId) {
        return validarSuperAdminPorId(superAdminId)
                .then(solicitacaoRepository.findById(idSolicitacao))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(solicitacao -> {
                    if (solicitacao.getStatus() != StatusPermissao.PENDENTE) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
                    }
                    solicitacao.setStatus(StatusPermissao.APROVADO);
                    return solicitacaoRepository.save(solicitacao)
                            .flatMap(solicitacaoAtualizada -> usuarioRepository.findById(solicitacaoAtualizada.getIdUsuario())
                                    .flatMap(usuario -> {
                                        usuario.setTipoUsuario(TipoUsuario.ADMIN);
                                        return usuarioRepository.save(usuario)
                                                .map(u -> SolicitacaoPermissaoDTO.fromEntity(solicitacaoAtualizada, u.getNome()));
                                    }));
                });
    }

    public Mono<SolicitacaoPermissaoDTO> negarSolicitacao(Long idSolicitacao, String motivo, Long superAdminId) {
        return validarSuperAdminPorId(superAdminId)
                .then(solicitacaoRepository.findById(idSolicitacao))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(solicitacao -> {
                    if (solicitacao.getStatus() != StatusPermissao.PENDENTE) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
                    }
                    solicitacao.setStatus(StatusPermissao.NEGADO);
                    solicitacao.setMotivoNegacao(motivo);
                    return solicitacaoRepository.save(solicitacao)
                            .flatMap(solicitacaoAtualizada -> usuarioRepository.findById(solicitacaoAtualizada.getIdUsuario())
                                    .map(usuario -> SolicitacaoPermissaoDTO.fromEntity(solicitacaoAtualizada, usuario.getNome())));
                });
    }

    public Flux<SolicitacaoPermissaoDTO> minhasSolicitacoes(Long idUsuario) {
        return solicitacaoRepository.findByIdUsuario(idUsuario)
                .flatMap(solicitacao -> usuarioRepository.findById(idUsuario)
                        .map(usuario -> SolicitacaoPermissaoDTO.fromEntity(solicitacao, usuario.getNome())));
    }
}
