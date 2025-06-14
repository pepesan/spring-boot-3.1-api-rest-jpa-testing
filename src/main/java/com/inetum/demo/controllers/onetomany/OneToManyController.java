package com.inetum.demo.controllers.onetomany;

import com.inetum.demo.domain.onetomany.Gender;
import com.inetum.demo.services.onetomany.OneToManyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/onetomany")
public class OneToManyController {
    OneToManyService oneToManyService;

    @Autowired
    OneToManyController(OneToManyService oneToManyService){
        this.oneToManyService = oneToManyService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Gender>> index(){
        return new ResponseEntity<>(
                this.oneToManyService.doSomething(),
                HttpStatus.OK);
    }
    /*
    [
      {
        "id": 1,
        "name": "Fantasía",
        "books": [
          {
            "id": 2,
            "title": "Mort"
          },
          {
            "id": 1,
            "title": "El Color de la Magia"
          }
        ]
      },
      {
        "id": 2,
        "name": "Medieval",
        "books": [
          {
            "id": 3,
            "title": "Mort"
          }
        ]
      }
    ]
     */
}
