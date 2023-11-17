package com.inetum.demo.repositories.manytomany;

import com.inetum.demo.domain.manytomany.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticiaRepository extends JpaRepository<Noticia,Long> {
}
