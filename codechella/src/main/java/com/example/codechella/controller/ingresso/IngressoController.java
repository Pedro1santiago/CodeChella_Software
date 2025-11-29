package com.example.codechella.controller.ingresso;


import com.example.codechella.models.ingresso.IngressoDTO;
import com.example.codechella.serivce.ingressoService.IngressoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/ingressos")
public class IngressoController {

    @Autowired
    private IngressoService ingressoService;

    @PostMapping
    public Mono<IngressoDTO> cadastrar(@RequestBody IngressoDTO ingressosDTO) {
        return ingressoService.cadastrarIngresso(ingressosDTO);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<IngressoDTO> listarIngressos() {
        return ingressoService.listarTodos();
    }

    @PostMapping("/comprar")
    public Flux<IngressoDTO> venderIngresso(@RequestParam Long eventoId, @RequestParam int quantidade){
        return ingressoService.venderIngresso(eventoId, quantidade);
    }

    @PutMapping("/Cancelar/{id}")
    public Mono<IngressoDTO> cancelarIngresso(@PathVariable Long id){
        return ingressoService.cancelarIngresso(id);
    }

}

