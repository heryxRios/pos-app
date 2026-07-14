package com.tienda.pos.controller;

import com.tienda.pos.dto.VentaRequestDTO;
import com.tienda.pos.model.Producto;
import com.tienda.pos.model.Venta;
import com.tienda.pos.service.ProductoService;
import com.tienda.pos.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ventas")
public class VentaController {

    private final ProductoService productoService;
    private final VentaService ventaService;

    /** Pantalla principal del punto de venta */
    @GetMapping("/nueva")
    public String nuevaVenta(Model model) {
        List<Producto> productos = productoService.listarActivos();
        model.addAttribute("productos", productos);
        return "ventas/nueva";
    }

    /** Endpoint AJAX: registrar la venta desde el carrito del POS */
    @PostMapping("/api/registrar")
    @ResponseBody
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequestDTO request, Authentication auth) {
        try {
            Venta venta = ventaService.registrarVenta(request, auth.getName());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "ventaId", venta.getId(),
                    "total", venta.getTotal()
            ));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    /** Endpoint AJAX: buscar productos en tiempo real para el POS */
    @GetMapping("/api/buscar-productos")
    @ResponseBody
    public List<Producto> buscarProductos(@RequestParam(required = false, defaultValue = "") String q) {
        if (q.isBlank()) {
            return productoService.listarActivos();
        }
        return productoService.buscarPorNombre(q);
    }

    /** Historial de ventas */
    @GetMapping("/historial")
    public String historial(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            Model model) {

        LocalDateTime inicio = desde != null ? LocalDateTime.parse(desde + "T00:00:00")
                : LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = hasta != null ? LocalDateTime.parse(hasta + "T23:59:59")
                : LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        model.addAttribute("ventas", ventaService.historialEntreFechas(inicio, fin));
        return "ventas/historial";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarVenta(@PathVariable Long id) {
        ventaService.cancelarVenta(id);
        return "redirect:/ventas/historial";
    }
}
