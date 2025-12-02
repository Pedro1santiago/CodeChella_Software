package com.example.codechella.serivce.superAdminService;

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

    // Lista todos os usuários comuns e admins
    public Flux<UsuarioResponseDTO> listarUsuarios(Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .thenMany(usuarioRepository.findAll().map(UsuarioMapper::toDTO));
    }

    // Remove um usuário
    public Mono<Void> removerUsuario(Long id, Long superAdminId) {
        return validarSuperPorId(superAdminId)
                .then(usuarioRepository.deleteById(id));
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