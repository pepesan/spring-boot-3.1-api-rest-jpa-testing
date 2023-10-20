package com.inetum.demo.controllers.onetoone;

import com.inetum.demo.domain.onetoone.Address;
import com.inetum.demo.domain.onetoone.Order;
import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.services.onetoone.OneToOneBidirectionalService;
import com.inetum.demo.services.onetoone.OneToOneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/onetoonebi/")
public class OneToOneBidirectionalController {
    OneToOneBidirectionalService oneToOneBidirectionalService;

    OneToOneBidirectionalController(
            OneToOneBidirectionalService oneToOneBidirectionalService
    ){
        this.oneToOneBidirectionalService = oneToOneBidirectionalService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> index(){
        return new ResponseEntity<>(
                this.oneToOneBidirectionalService.doSomething(),
                HttpStatus.OK
        );
    }
    @GetMapping("/address")
    public ResponseEntity<List<Address>> indexAddress(){
        return new ResponseEntity<>(
                this.oneToOneBidirectionalService.doSomethingAddress(),
                HttpStatus.OK
        );
    }





}
