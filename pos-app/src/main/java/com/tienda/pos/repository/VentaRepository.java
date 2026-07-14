package com.tienda.pos.repository;

import com.tienda.pos.model.EstadoVenta;
import com.tienda.pos.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);

    List<Venta> findByEstado(EstadoVenta estado);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
           "WHERE v.fecha BETWEEN :desde AND :hasta AND v.estado = 'COMPLETADA'")
    BigDecimal calcularTotalVentasEntreFechas(@Param("desde") LocalDateTime desde,
                                               @Param("hasta") LocalDateTime hasta);

    @Query("SELECT dv.producto.nombre, SUM(dv.cantidad) as totalVendido " +
           "FROM DetalleVenta dv " +
           "WHERE dv.venta.estado = 'COMPLETADA' " +
           "GROUP BY dv.producto.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();

    List<Venta> findByUsuarioIdAndFechaBetween(Long usuarioId, LocalDateTime desde, LocalDateTime hasta);
}
