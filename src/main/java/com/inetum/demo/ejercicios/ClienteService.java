package com.inetum.demo.ejercicios;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ClienteService {
    public List<Cliente> findAll();
    public Cliente save(Cliente cliente);

    Optional<Cliente> findById(Long id);
    public void delete(Cliente cliente);
}
