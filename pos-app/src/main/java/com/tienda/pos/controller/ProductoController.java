package com.tienda.pos.controller;

import com.tienda.pos.model.Categoria;
import com.tienda.pos.model.Producto;
import com.tienda.pos.repository.CategoriaRepository;
import com.tienda.pos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("stockBajo", productoService.productosConStockBajo());
        return "productos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "productos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Producto producto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "productos/formulario";
        }
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return "redirect:/productos";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        categoriaRepository.save(categoria);
        return "redirect:/productos/nuevo";
    }
}
