package com.example.codechella.models.evento;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTipoEventoConverter implements Converter<String, TipoEvento> {
    @Override
    public TipoEvento convert(String source) {
        if (source == null) return null;
        try {
            return TipoEvento.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Tipo de evento inválido: " + source);
        }
    }
}