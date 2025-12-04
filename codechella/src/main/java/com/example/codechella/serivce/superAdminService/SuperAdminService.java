package com.example.codechella.serivce.superAdminService;

import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.users.*;
import com.example.codechella.repository.EventoRepository;
import com.example.codechella.repository.SuperAdminRepository;
import com.example.codechella.repository.UsuarioAdminRepository;
import com.example.codechella.repository.UsuarioRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class SuperAdminService {

    @Autowired
    SuperAdminRepository superAdminRepository;

    @Autowired
    UsuarioAdminRepository usuarioAdminRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Extrai o ID do super admin do Authorization Bearer Token
    public Long extrairIdSuperAdminDoHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();


            String subject = claims.getSubject();
            if (subject == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token sem subject");
            }

            return Long.parseLong(subject);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
    }

    // Verifica se o ID pertence a um super administrador
    private Mono<SuperAdmin> validarSuperPorId(Long superAdminId) {
        return superAdminRepository.findById(superAdminId)
                .filter(s -> s.getTipoUsuario() == TipoUsuario.SUPER)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado")));
    }

    // Cria um novo administrador
    public Mono<UsuarioAdminDTO> criarAdmin(Long superAdminId, UsuarioAdminDTO adminDTO) {
        return validarSuperPorId(superAdminId)
                .then(Mono.defer(() -> {
                    var entity = adminDTO.toEntity();
                    if (entity.getSenha() != null) {
                        entity.setSenha(passwordEncoder.encode(entity.getSenha()));
                    }
                    return usuarioAdminRepository.save(entity).map(UsuarioAdminDTO::toDTO);
                }));
    }

    // Lista todos os administradores
    public Flux<UsuarioAdminDTO> listarAdmins(Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .thenMany(usuarioAdminRepository.findAll().map(UsuarioAdminDTO::toDTO));
    }

    // Remove um admin específico
    public Mono<Void> removerAdmin(Long id, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(usuarioAdminRepository.deleteById(id));
    }

    // Lista todos os usuários comuns e admins ATIVOS
    public Flux<UsuarioResponseDTO> listarUsuarios(Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .thenMany(usuarioRepository.findAllAtivos().map(UsuarioMapper::toDTO));
    }

    // Remove um usuário (SOFT DELETE)
    public Mono<Void> removerUsuario(Long id, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(usuarioRepository.findById(id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                .flatMap(usuario -> {
                    // Soft delete do usuário
                    usuario.setDeletado(true);
                    usuario.setDataExclusao(LocalDateTime.now());

                    return usuarioRepository.save(usuario)
                            // Cancelar todos os eventos criados por este usuário
                            .then(eventoRepository.findAll()
                                    .filter(evento -> evento.getIdAdminCriador() != null
                                            && evento.getIdAdminCriador().equals(id))
                                    .flatMap(evento -> {
                                        evento.setCancelado(true);
                                        return eventoRepository.save(evento);
                                    })
                                    .then());
                });
    }

    // Exclui qualquer evento do sistema
    public Mono<Void> excluirEventoQualquer(Long idEvento, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(eventoRepository.findById(idEvento))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                .flatMap(eventoRepository::delete);
    }

    // Promove um usuário para admin
    public Mono<UsuarioResponseDTO> promoverParaAdmin(Long idUsuario, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(usuarioRepository.findById(idUsuario))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                .flatMap(usuario -> {
                    if (usuario.getTipoUsuario() != TipoUsuario.USER) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já é administrador"));
                    }
                    usuario.setTipoUsuario(TipoUsuario.ADMIN);
                    return usuarioRepository.save(usuario);
                })
                .map(UsuarioMapper::toDTO);
    }

    // Rebaixa um administrador para usuário comum
    public Mono<UsuarioResponseDTO> rebaixarParaUser(Long idUsuario, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(usuarioRepository.findById(idUsuario))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                .flatMap(usuario -> {
                    if (usuario.getTipoUsuario() != TipoUsuario.ADMIN) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário não é admin"));
                    }
                    usuario.setTipoUsuario(TipoUsuario.USER);
                    return usuarioRepository.save(usuario);
                })
                .map(UsuarioMapper::toDTO);
    }

    // Lista usuários excluídos (soft delete)
    public Flux<UsuarioResponseDTO> listarUsuariosExcluidos(Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .thenMany(usuarioRepository.findAllDeletados()
                        .map(UsuarioMapper::toDTO));
    }

    // Lista eventos cancelados de um usuário específico
    public Flux<EventoDTO> listarEventosCancelados(Long idUsuario, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .thenMany(eventoRepository.findEventosCanceladosPorUsuario(idUsuario)
                        .map(EventoDTO::toDto));
    }

    // Reativa um evento cancelado e o transfere para o Super Admin
    public Mono<EventoDTO> reativarEvento(Long idEvento, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(eventoRepository.findById(idEvento))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                .flatMap(evento -> {
                    // Reativa o evento e transfere para Super Admin
                    evento.setCancelado(false);
                    evento.setIdAdminCriador(superAdminId);
                    return eventoRepository.save(evento);
                })
                .map(EventoDTO::toDto);
    }

    public Mono<TipoUsuario> obterTipoDoUsuario(Long usuarioId) {
        //Primeiro verifica se é super admin
        return superAdminRepository.findById(usuarioId)
                .map(SuperAdmin::getTipoUsuario)
                .switchIfEmpty(
                        //verifica se é admin
                        usuarioAdminRepository.findById(usuarioId)
                                .map(UserAdmin::getTipoUsuario)
                                .switchIfEmpty(
                                        //verifica se é usuário comum
                                        usuarioRepository.findById(usuarioId)
                                                .map(Usuario::getTipoUsuario)
                                                .switchIfEmpty(Mono.error(
                                                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")
                                                ))
                                )
                );
    }

}
