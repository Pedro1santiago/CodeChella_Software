package com.example.codechella.serivce.eventoService;

import com.example.codechella.models.evento.Evento;
import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.evento.StatusEvento;
import com.example.codechella.models.evento.TipoEvento;
import com.example.codechella.models.users.TipoUsuario;
import com.example.codechella.models.users.SuperAdmin;
import com.example.codechella.models.users.Usuario;
import com.example.codechella.repository.EventoRepository;
import com.example.codechella.repository.SuperAdminRepository;
import com.example.codechella.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventoService {

    @Autowired
    private EventoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    // Valida se é ADMIN ou SUPER
    private Mono<TipoUsuario> validarCriador(Long usuarioId) {
        // procura super admin
        return superAdminRepository.findById(usuarioId)
                .map(SuperAdmin::getTipoUsuario)
                .switchIfEmpty(
                        // procura admin comum
                        usuarioRepository.findById(usuarioId)
                                .map(Usuario::getTipoUsuario)
                )
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado")))
                .flatMap(tipo -> {
                    if (tipo == TipoUsuario.ADMIN || tipo == TipoUsuario.SUPER) {
                        return Mono.just(tipo);
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem realizar esta operação"));
                });
    }

    // Lista todos os eventos
    public Flux<EventoDTO> listarTodos() {
        return repository.findAll().map(EventoDTO::toDto);
    }

    // Buscar evento por ID
    public Mono<EventoDTO> buscarPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                .map(EventoDTO::toDto);
    }

    // Criar evento (ADMIN e SUPER)
    public Mono<EventoDTO> cadastrarEvento(Long usuarioId, EventoDTO dto) {
        return validarCriador(usuarioId)
                .flatMap(tipo -> {
                    Evento evento = dto.toEntity();
                    evento.setStatusEvento(StatusEvento.ABERTO);
                    evento.setIdAdminCriador(usuarioId); // salva o criador
                    return repository.save(evento).map(EventoDTO::toDto);
                });
    }

    // Atualizar evento
    public Mono<EventoDTO> atualizarId(Long id, EventoDTO dto, Long usuarioId) {
        return validarCriador(usuarioId)
                .flatMap(tipo -> repository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                        .flatMap(evento -> {

                            // super admin pode tudo
                            if (tipo != TipoUsuario.SUPER &&
                                    !evento.getIdAdminCriador().equals(usuarioId)) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode atualizar eventos que criou");
                            }

                            evento.setTipo(dto.tipo());
                            evento.setNome(dto.nome());
                            evento.setData(dto.data());
                            evento.setDescricao(dto.descricao());
                            evento.setNumeroIngressosDisponiveis(dto.numeroIngressosDisponiveis());

                            return repository.save(evento).map(EventoDTO::toDto);
                        }));
    }

    // Cancelar evento
    public Mono<EventoDTO> cancelarEvento(Long id, Long usuarioId) {
        return validarCriador(usuarioId)
                .flatMap(tipo -> repository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                        .flatMap(evento -> {

                            if (tipo != TipoUsuario.SUPER &&
                                    !evento.getIdAdminCriador().equals(usuarioId)) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode cancelar eventos que criou");
                            }

                            evento.setStatusEvento(StatusEvento.FECHADO);
                            return repository.save(evento).map(EventoDTO::toDto);
                        }));
    }

    // excluir evento
    public Mono<Void> excluir(Long id, Long usuarioId) {
        return validarCriador(usuarioId)
                .flatMap(tipo -> repository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                        .flatMap(evento -> {

                            if (tipo != TipoUsuario.SUPER &&
                                    !evento.getIdAdminCriador().equals(usuarioId)) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode excluir eventos que criou");
                            }

                            return repository.delete(evento);
                        }));
    }

    // Busca por categoria
    public Flux<EventoDTO> obterPorTipo(TipoEvento tipo) {
        return repository.findByTipo(tipo).map(EventoDTO::toDto);
    }
}
