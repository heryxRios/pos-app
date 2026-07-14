package com.tienda.pos.service;

import com.tienda.pos.model.CorteCaja;
import com.tienda.pos.model.Usuario;
import com.tienda.pos.repository.CorteCajaRepository;
import com.tienda.pos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CajaService {

    private final CorteCajaRepository corteCajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteService reporteService;

    public Optional<CorteCaja> cajaAbiertaActual() {
        return corteCajaRepository.findByAbiertaTrue();
    }

    @Transactional
    public CorteCaja abrirCaja(String emailUsuario, BigDecimal montoInicial) {
        if (cajaAbiertaActual().isPresent()) {
            throw new IllegalStateException("Ya existe una caja abierta. Ciérrala antes de abrir otra.");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CorteCaja corte = CorteCaja.builder()
                .usuario(usuario)
                .fechaApertura(LocalDateTime.now())
                .montoInicial(montoInicial)
                .abierta(true)
                .build();

        return corteCajaRepository.save(corte);
    }

    @Transactional
    public CorteCaja cerrarCaja(Long corteCajaId, BigDecimal montoContado) {
        CorteCaja corte = corteCajaRepository.findById(corteCajaId)
                .orElseThrow(() -> new IllegalArgumentException("Corte de caja no encontrado"));

        BigDecimal totalVentasEfectivo = reporteService.totalVentasEntreFechas(
                corte.getFechaApertura().toLocalDate(),
                LocalDateTime.now().toLocalDate()
        );

        BigDecimal montoEsperado = corte.getMontoInicial().add(totalVentasEfectivo);

        corte.setFechaCierre(LocalDateTime.now());
        corte.setMontoFinalEsperado(montoEsperado);
        corte.setMontoFinalContado(montoContado);
        corte.setDiferencia(montoContado.subtract(montoEsperado));
        corte.setAbierta(false);

        return corteCajaRepository.save(corte);
    }
}
