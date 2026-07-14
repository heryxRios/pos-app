package com.tienda.pos.service;

import com.tienda.pos.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepository ventaRepository;

    public BigDecimal totalVentasDelDia(LocalDate dia) {
        LocalDateTime inicio = LocalDateTime.of(dia, LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(dia, LocalTime.MAX);
        return ventaRepository.calcularTotalVentasEntreFechas(inicio, fin);
    }

    public BigDecimal totalVentasEntreFechas(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = LocalDateTime.of(desde, LocalTime.MIN);
        LocalDateTime fin = LocalDateTime.of(hasta, LocalTime.MAX);
        return ventaRepository.calcularTotalVentasEntreFechas(inicio, fin);
    }

    public List<Object[]> productosMasVendidos() {
        return ventaRepository.findProductosMasVendidos();
    }
}
