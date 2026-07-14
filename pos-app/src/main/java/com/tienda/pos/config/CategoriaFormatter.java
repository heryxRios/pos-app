package com.tienda.pos.config;

import com.tienda.pos.model.Categoria;
import com.tienda.pos.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

/**
 * Permite que los formularios Thymeleaf envien solo el ID de la categoria
 * (por ejemplo desde un <select>) y Spring lo convierta automaticamente
 * a la entidad Categoria completa al hacer el binding del formulario (parse),
 * y tambien que el <select> marque correctamente la opcion ya seleccionada
 * al editar un producto existente (print).
 */
@Component
@RequiredArgsConstructor
public class CategoriaFormatter implements Formatter<Categoria> {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria parse(String text, Locale locale) throws ParseException {
        if (text == null || text.isBlank()) {
            return null;
        }
        return categoriaRepository.findById(Long.valueOf(text)).orElse(null);
    }

    @Override
    public String print(Categoria categoria, Locale locale) {
        return categoria != null && categoria.getId() != null ? categoria.getId().toString() : "";
    }
}
