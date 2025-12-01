package com.example.codechella.controller.auth;

import com.example.codechella.models.users.LoginRequest;
import com.example.codechella.models.users.SuperAdminDTO;
import com.example.codechella.models.users.SuperAdminLoginRequest;
import com.example.codechella.models.users.UsuarioRegisterRequest;
import com.example.codechella.models.users.UsuarioResponseDTO;
import com.example.codechella.serivce.superAdminAuth.SuperAdminAuthService;
import com.example.codechella.serivce.usuario.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final SuperAdminAuthService superAdminAuthService;

    public AuthController(UsuarioService usuarioService, SuperAdminAuthService superAdminAuthService) {
        this.usuarioService = usuarioService;
        this.superAdminAuthService = superAdminAuthService;
    }

    @PostMapping("/usuario/registrar")
    public Mono<UsuarioResponseDTO> registerUsuario(@RequestBody UsuarioRegisterRequest request) {
        log.info("[CONTROLLER] Requisição de cadastro recebida para email: {}", request.email());
        return usuarioService.cadastrar(request)
                .doOnError(err -> log.error("[CONTROLLER] Erro no cadastro do usuário: {}", err.getMessage(), err));
    }

    @PostMapping("/usuario/login")
    public Mono<UsuarioResponseDTO> loginUsuario(@RequestBody LoginRequest login) {
        log.info("[CONTROLLER] Requisição de login recebida para email: {}", login.email());
        return usuarioService.login(login)
                .doOnError(err -> log.error("[CONTROLLER] Erro no login do usuário: {}", err.getMessage(), err));
    }

    @PostMapping("/super-admin/login")
    public Mono<SuperAdminDTO> loginSuperAdmin(@RequestBody SuperAdminLoginRequest login) {
        log.info("[CONTROLLER] Requisição de login de Super Admin recebida para email: {}", login.email());
        return superAdminAuthService.login(login)
                .doOnError(err -> log.error("[CONTROLLER] Erro no login do Super Admin: {}", err.getMessage(), err));
    }
}
