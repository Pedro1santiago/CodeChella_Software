package com.example.codechella.serivce.eventoService;

import com.example.codechella.models.evento.Evento;
import com.example.codechella.models.evento.EventoDTO;
import com.example.codechella.models.evento.StatusEvento;
import com.example.codechella.models.evento.TipoEvento;
import com.example.codechella.models.users.TipoUsuario;
import com.example.codechella.repository.EventoRepository;
import com.example.codechella.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoService.class);

    @Autowired
    private EventoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Mono<TipoUsuario> validarCriador(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado")))
                .flatMap(usuario -> {
                    TipoUsuario tipo = usuario.getTipoUsuario();
                    if (tipo == TipoUsuario.ADMIN || tipo == TipoUsuario.SUPER) {
                        return Mono.just(tipo);
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem realizar esta operação"));
                });
    }

    public Flux<EventoDTO> listarTodosAtivos() {
        return repository.findAllAtivos().map(EventoDTO::toDto);
    }

    public Mono<EventoDTO> buscarPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                .map(EventoDTO::toDto);
    }

    public Mono<EventoDTO> cadastrarEvento(Long usuarioId, EventoDTO dto) {
        return validarCriador(usuarioId)
                .flatMap(tipo -> {
                    Evento evento = dto.toEntity();
                    evento.setStatusEvento(StatusEvento.ABERTO);
                    evento.setIdAdminCriador(usuarioId);
                    return repository.save(evento).map(EventoDTO::toDto);
                });
    }

    public Mono<EventoDTO> atualizarId(Long id, EventoDTO dto, Long usuarioId) {
        return validarCriador(usuarioId)
                .flatMap(tipo -> repository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado")))
                        .flatMap(evento -> {
                            if (tipo != TipoUsuario.SUPER &&
                                    !evento.getIdAdminCriador().equals(usuarioId)) {
                                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode atualizar eventos que criou");
                            }

                            evento.setTipo(dto.tipo());
                            evento.setNome(dto.nome());
                            evento.setData(dto.data());
                            evento.setLocal(dto.local());
                            evento.setDescricao(dto.descricao());
                            evento.setPreco(dto.preco());
                            evento.setNumeroIngressosDisponiveis(dto.ingressosDisponiveis());
                            return repository.save(evento).map(EventoDTO::toDto);
                        }));
    }

    public Mono<EventoDTO> cancelarEvento(Long eventoId, Long usuarioId) {
        log.info("[CANCELAR-SERVICE] Iniciando - eventoId: {}, usuarioId: {}", eventoId, usuarioId);

        return validarCriador(usuarioId)
                .doOnNext(tipo -> log.info("[CANCELAR-SERVICE] Tipo de usuário validado: {}", tipo))
                .flatMap(tipo -> repository.findById(eventoId)
                        .doOnNext(evento -> log.info("[CANCELAR-SERVICE] Evento encontrado - id: {}, criador: {}, status: {}",
                                evento.getId(), evento.getIdAdminCriador(), evento.getStatusEvento()))
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("[CANCELAR-SERVICE] Evento {} não encontrado", eventoId);
                            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado"));
                        }))
                        .flatMap(evento -> {
                            log.info("[CANCELAR-SERVICE] Validando permissões - tipo: {}, criador: {}, usuarioId: {}",
                                    tipo, evento.getIdAdminCriador(), usuarioId);

                            if (tipo != TipoUsuario.SUPER && !evento.getIdAdminCriador().equals(usuarioId)) {
                                log.warn("[CANCELAR-SERVICE] Usuário {} não tem permissão para cancelar evento {}", usuarioId, eventoId);
                                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode cancelar eventos que criou"));
                            }

                            log.info("[CANCELAR-SERVICE] Alterando status para CANCELADO");
                            evento.setStatusEvento(StatusEvento.CANCELADO);
                            return repository.save(evento).map(EventoDTO::toDto);
                        })
                )
                .doOnSuccess(dto -> log.info("[CANCELAR-SERVICE] Evento {} cancelado com sucesso", eventoId))
                .doOnError(e -> log.error("[CANCELAR-SERVICE] Erro ao cancelar evento {}: {} - {}",
                        eventoId, e.getClass().getSimpleName(), e.getMessage(), e));
    }

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

    public Flux<EventoDTO> obterPorTipo(TipoEvento tipo) {
        return repository.findByTipo(tipo).map(EventoDTO::toDto);
    }
}
