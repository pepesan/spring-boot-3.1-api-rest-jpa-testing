package com.inetum.demo.services.manytomany;

import com.inetum.demo.domain.manytomany.Etiqueta;
import com.inetum.demo.domain.manytomany.Noticia;
import com.inetum.demo.repositories.manytomany.EtiquetaRepository;
import com.inetum.demo.repositories.manytomany.NoticiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManyToManyUnidirectionalService {
    private NoticiaRepository noticiaRepository;
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    public ManyToManyUnidirectionalService(
            NoticiaRepository noticiaRepository,
            EtiquetaRepository etiquetaRepository){
        this.etiquetaRepository = etiquetaRepository;
        this.noticiaRepository = noticiaRepository;

    }
    @Transactional
    public List<Noticia> doSomething(){
        Noticia noticia = new Noticia();
        noticia.setTitulo("Noticia 1");
        this.noticiaRepository.save(noticia);
        Etiqueta etiqueta = new Etiqueta();
        etiqueta.setNombre("Etiqueta 1");
        this.etiquetaRepository.save(etiqueta);
        noticia.getEtiquetas().add(etiqueta);
        this.noticiaRepository.save(noticia);
        return this.noticiaRepository.findAll();
    }
}
