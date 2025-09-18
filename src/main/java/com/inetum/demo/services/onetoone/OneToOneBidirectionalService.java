package com.inetum.demo.services.onetoone;

import com.inetum.demo.domain.onetoone.Address;
import com.inetum.demo.domain.onetoone.Order;
import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.repositories.onetoone.AddressesRepository;
import com.inetum.demo.repositories.onetoone.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class OneToOneBidirectionalService {
    OrderRepository orderRepository;
    AddressesRepository addressesRepository;

    @Autowired
    OneToOneBidirectionalService(
            OrderRepository orderRepository,
            AddressesRepository addressesRepository
    ){
        this.orderRepository = orderRepository;
        this.addressesRepository = addressesRepository;
    }

    @Transactional
    public List<Order> doSomething() {
        this.orderRepository.deleteAll();
        this.addressesRepository.deleteAll();
        Order order = new Order();
        order.setCode("H0ck");
        this.orderRepository.save(order);
        Address address = new Address();
        address.setStreet("Plaza Mayor");
        order.setBillingAddress(address);
        address.setOrder(order);
        this.orderRepository.save(order);
        return this.orderRepository.findAll();
    }

    @Transactional
    public List<Address> doSomethingAddress() {
        this.orderRepository.deleteAll();
        this.addressesRepository.deleteAll();
        Order order = new Order();
        order.setCode("H0ck");
        this.orderRepository.save(order);
        Address address = new Address();
        address.setStreet("Plaza Mayor");
        order.setBillingAddress(address);
        //this.orderRepository.save(order);
        address.setOrder(order);
        this.addressesRepository.save(address);
        List<Address> listado = this.addressesRepository.findAll();
        // System.out.println(listado.getFirst().getOrder().getCode());
        return listado;
    }
}
