package com.tienda.pos.dto;

import com.tienda.pos.model.MetodoPago;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequestDTO {
    private Long clienteId;
    private List<ItemCarritoDTO> items;
    private MetodoPago metodoPago;
    private BigDecimal descuento;
}
