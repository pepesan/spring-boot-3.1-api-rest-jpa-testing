package com.inetum.demo.controllers.manytomany;

import com.inetum.demo.domain.manytomany.Noticia;
import com.inetum.demo.services.manytomany.ManyToManyUnidirectionalService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manytomanyuni")
public class ManyToManyUnidirectionalController {

    private ManyToManyUnidirectionalService service;

    @Autowired
    public ManyToManyUnidirectionalController(
            ManyToManyUnidirectionalService service){
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Noticia>> index(){
        return new ResponseEntity<>(
                this.service.doSomething(),
                HttpStatus.OK);
    }
    /*
    Respuesta JSON
    [
      {
        "id": 1,
        "titulo": "Noticia 1",
        "etiquetas": [
          {
            "id": 1,
            "nombre": "Etiqueta 1"
          }
        ]
      }
    ]
     */


}
