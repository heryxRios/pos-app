package com.tienda.pos.controller;

import com.tienda.pos.service.CajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/caja")
public class CajaController {

    private final CajaService cajaService;

    @GetMapping
    public String pantallaCaja(Model model) {
        model.addAttribute("cajaAbierta", cajaService.cajaAbiertaActual().orElse(null));
        return "caja/caja";
    }

    @PostMapping("/abrir")
    public String abrir(@RequestParam BigDecimal montoInicial, Authentication auth) {
        cajaService.abrirCaja(auth.getName(), montoInicial);
        return "redirect:/caja";
    }

    @PostMapping("/{id}/cerrar")
    public String cerrar(@PathVariable Long id, @RequestParam BigDecimal montoContado) {
        cajaService.cerrarCaja(id, montoContado);
        return "redirect:/caja";
    }
}
