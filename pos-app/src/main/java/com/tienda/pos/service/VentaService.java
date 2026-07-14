package com.tienda.pos.service;

import com.tienda.pos.dto.ItemCarritoDTO;
import com.tienda.pos.dto.VentaRequestDTO;
import com.tienda.pos.model.*;
import com.tienda.pos.repository.ClienteRepository;
import com.tienda.pos.repository.UsuarioRepository;
import com.tienda.pos.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final BigDecimal PORCENTAJE_IMPUESTO = new BigDecimal("0.16"); // 16% IVA

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoService productoService;

    /**
     * Registra una venta completa: valida stock, calcula totales,
     * descuenta inventario y guarda la transaccion.
     */
    @Transactional
    public Venta registrarVenta(VentaRequestDTO request, String emailUsuario) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacio");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Cliente cliente = null;
        if (request.getClienteId() != null) {
            cliente = clienteRepository.findById(request.getClienteId()).orElse(null);
        }

        Venta venta = Venta.builder()
                .fecha(LocalDateTime.now())
                .usuario(usuario)
                .cliente(cliente)
                .metodoPago(request.getMetodoPago())
                .estado(EstadoVenta.COMPLETADA)
                .descuento(request.getDescuento() != null ? request.getDescuento() : BigDecimal.ZERO)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCarritoDTO item : request.getItems()) {
            Producto producto = productoService.obtenerPorId(item.getProductoId());

            // Descuenta inventario (lanza excepcion si no hay stock suficiente)
            productoService.descontarStock(producto.getId(), item.getCantidad());

            BigDecimal subtotalItem = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            DetalleVenta detalle = DetalleVenta.builder()
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .subtotal(subtotalItem)
                    .build();

            venta.agregarDetalle(detalle);
            subtotal = subtotal.add(subtotalItem);
        }

        BigDecimal descuento = venta.getDescuento();
        BigDecimal baseImponible = subtotal.subtract(descuento);
        BigDecimal impuesto = baseImponible.multiply(PORCENTAJE_IMPUESTO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = baseImponible.add(impuesto);

        venta.setSubtotal(subtotal);
        venta.setImpuesto(impuesto);
        venta.setTotal(total);

        return ventaRepository.save(venta);
    }

    @Transactional
    public void cancelarVenta(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("La venta ya esta cancelada");
        }

        // Regresa el stock al inventario
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }

        venta.setEstado(EstadoVenta.CANCELADA);
        ventaRepository.save(venta);
    }

    public List<Venta> historialEntreFechas(LocalDateTime desde, LocalDateTime hasta) {
        return ventaRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
    }

    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada: " + id));
    }
}
