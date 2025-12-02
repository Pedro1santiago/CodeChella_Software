package com.example.codechella.controller.auth;

import com.example.codechella.models.users.*;
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
        return usuarioService.cadastrar(request);
    }

    @PostMapping("/usuario/login")
    public Mono<UsuarioResponseDTO> loginUsuario(@RequestBody LoginRequest login) {
        return usuarioService.login(login);
    }

    @PostMapping("/super-admin/login")
    public Mono<SuperAdminDTO> loginSuperAdmin(@RequestBody SuperAdminLoginRequest login) {
        return superAdminAuthService.login(login);
    }
}
