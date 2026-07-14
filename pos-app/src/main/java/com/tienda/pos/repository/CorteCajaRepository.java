package com.tienda.pos.repository;

import com.tienda.pos.model.CorteCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorteCajaRepository extends JpaRepository<CorteCaja, Long> {
    Optional<CorteCaja> findByAbiertaTrue();
}
