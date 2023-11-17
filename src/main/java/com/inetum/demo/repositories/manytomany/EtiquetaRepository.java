package com.inetum.demo.repositories.manytomany;

import com.inetum.demo.domain.manytomany.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
}
