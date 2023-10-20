package com.inetum.demo.controllers.manytomany;

import com.inetum.demo.domain.manytomany.User;
import com.inetum.demo.services.manytomany.ManyToManyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manytomany")
public class ManyToManyController {

    ManyToManyService manyToManyService;

    @Autowired
    ManyToManyController(
            ManyToManyService manyToManyService
    ){
        this.manyToManyService = manyToManyService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> index(){
        return new ResponseEntity<>(
                this.manyToManyService.doSomething(),
                HttpStatus.OK);
    }
}
