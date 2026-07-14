package com.tienda.pos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cortes_caja")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorteCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaApertura;

    private LocalDateTime fechaCierre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicial;

    @Column(precision = 10, scale = 2)
    private BigDecimal montoFinalEsperado;

    @Column(precision = 10, scale = 2)
    private BigDecimal montoFinalContado;

    @Column(precision = 10, scale = 2)
    private BigDecimal diferencia;

    @Builder.Default
    @Column(nullable = false)
    private boolean abierta = true;
}
