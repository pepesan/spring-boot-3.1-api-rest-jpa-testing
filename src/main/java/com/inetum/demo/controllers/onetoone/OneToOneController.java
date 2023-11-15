package com.inetum.demo.controllers.onetoone;

import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.services.onetoone.OneToOneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/onetoone/")
public class OneToOneController {
    OneToOneService oneToOneService;

    OneToOneController(
            OneToOneService oneToOneService
    ){
        this.oneToOneService = oneToOneService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Phone>> index(){
        return new ResponseEntity<>(
                this.oneToOneService.doSomething(),
                HttpStatus.OK
        );
    }
}
