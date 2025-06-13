package com.inetum.demo.controllers.onetomany;

import com.inetum.demo.domain.onetomany.Address2;
import com.inetum.demo.domain.onetomany.Gender;
import com.inetum.demo.domain.onetomany.Person;
import com.inetum.demo.services.onetomany.OneToManyBiService;
import com.inetum.demo.services.onetomany.OneToManyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/onetomanybi")
public class OneToManyBiController {
    OneToManyBiService oneToManyBiService;
    @Autowired
    OneToManyBiController(OneToManyBiService oneToManyBiService){
        this.oneToManyBiService = oneToManyBiService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Person>> index(){
        return new ResponseEntity<>(
                this.oneToManyBiService.doSomething(),
                HttpStatus.OK);
    }
    /*
    [
      {
        "id": 1,
        "name": "David",
        "addresses": [
          {
            "id": 1,
            "street": "Mayor",
            "city": "Salamanca"
          }
        ]
      }
    ]
     */
    @GetMapping("/address")
    public ResponseEntity<List<Address2>> index2(){
        return new ResponseEntity<>(
                this.oneToManyBiService.doSomething2(),
                HttpStatus.OK);
    }
}
