package com.example.codechella.serivce.usuario;

import com.example.codechella.models.users.*;
import com.example.codechella.repository.UsuarioRepository;
import com.example.codechella.repository.SuperAdminRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SuperAdminRepository superAdminRepository;
    private final String secretKey;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          SuperAdminRepository superAdminRepository,
                          @Value("${jwt.secret}") String secretKey) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.superAdminRepository = superAdminRepository;
        this.secretKey = secretKey;
    }

    public Mono<UsuarioResponseDTO> cadastrar(UsuarioRegisterRequest req) {
        log.info("[CADASTRO] Recebendo request: {}", req);

        if (!req.senha().equals(req.confirmarSenha())) {
            log.warn("[CADASTRO] Senhas não coincidem para email: {}", req.email());
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "As senhas não coincidem"));
        }

        return usuarioRepository.findByEmail(req.email())
                .flatMap(u -> Mono.<UsuarioResponseDTO>error(
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado")
                ))
                .switchIfEmpty(Mono.defer(() -> {
                    Usuario usuario = UsuarioMapper.toEntity(req);
                    usuario.setTipoUsuario(TipoUsuario.USER);
                    usuario.setSenha(passwordEncoder.encode(req.senha()));
                    usuario.setCriadoEm(LocalDateTime.now());

                    log.info("[CADASTRO] Criando usuário: {}", usuario.getEmail());

                    return usuarioRepository.save(usuario)
                            .doOnSuccess(u -> log.info("[CADASTRO] Usuário cadastrado com sucesso: {}", u.getEmail()))
                            .doOnError(err -> log.error("[CADASTRO] Erro ao salvar usuário", err))
                            .map(UsuarioMapper::toDTO);
                }));
    }

    public Mono<UsuarioResponseDTO> login(LoginRequest login) {
        log.info("[LOGIN] Tentativa de login para email: {}", login.email());

        return usuarioRepository.findByEmail(login.email())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                .flatMap(usuario -> {
                    if (!passwordEncoder.matches(login.senha(), usuario.getSenha())) {
                        log.warn("[LOGIN] Senha incorreta para email: {}", login.email());
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Senha incorreta"));
                    }

                    log.info("[LOGIN] Login bem-sucedido para email: {}", login.email());

                    // ===== GERAR JWT =====
                    String token = Jwts.builder()
                            .setSubject(usuario.getId().toString())
                            .claim("roles", List.of(usuario.getTipoUsuario().name()))
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                            .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                            .compact();

                    return Mono.just(new UsuarioResponseDTO(
                            usuario.getId(),
                            usuario.getNome(),
                            usuario.getEmail(),
                            usuario.getTipoUsuario(),
                            usuario.getCriadoEm(),
                            token
                    ));
                });
    }

    public Mono<Void> removerUsuario(Long id, Long superAdminId) {
        return superAdminRepository.findById(superAdminId)
                .filter(s -> s.getTipoUsuario() == TipoUsuario.SUPER)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado")))
                .then(usuarioRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")))
                        .flatMap(usuarioRepository::delete));
    }

    public Flux<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .map(UsuarioMapper::toDTO);
    }
}
