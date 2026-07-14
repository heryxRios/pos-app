package com.tienda.pos.controller;

import com.tienda.pos.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public String reportes(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            Model model) {

        LocalDate fechaDesde = desde != null ? LocalDate.parse(desde) : LocalDate.now();
        LocalDate fechaHasta = hasta != null ? LocalDate.parse(hasta) : LocalDate.now();

        model.addAttribute("totalVentas", reporteService.totalVentasEntreFechas(fechaDesde, fechaHasta));
        model.addAttribute("productosMasVendidos", reporteService.productosMasVendidos());
        model.addAttribute("desde", fechaDesde);
        model.addAttribute("hasta", fechaHasta);

        return "reportes/dashboard";
    }
}
