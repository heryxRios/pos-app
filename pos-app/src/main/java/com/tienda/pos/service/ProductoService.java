package com.tienda.pos.service;

import com.tienda.pos.model.Producto;
import com.tienda.pos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    public List<Producto> productosConStockBajo() {
        return productoRepository.findProductosConStockBajo();
    }

    @Transactional
    public Producto guardar(Producto producto) {
        if (producto.getId() == null && productoRepository.existsBySku(producto.getSku())) {
            throw new IllegalArgumentException("Ya existe un producto con el SKU: " + producto.getSku());
        }
        return productoRepository.save(producto);
    }

    @Transactional
    public void desactivar(Long id) {
        Producto producto = obtenerPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional
    public void descontarStock(Long productoId, int cantidad) {
        Producto producto = obtenerPorId(productoId);
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }
}
