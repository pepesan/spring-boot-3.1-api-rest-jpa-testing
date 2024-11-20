package com.inetum.demo.ejercicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService{
    private ClienteRepository clienteRepository;

    @Autowired
    public ClienteServiceImpl(ClienteRepository clienteRepository){
        this.clienteRepository= clienteRepository;
    }
    @Override
    public List<Cliente> findAll() {
        return this.clienteRepository.findAll();
    }

    @Override
    public Cliente save(Cliente cliente) {
        return this.clienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return this.clienteRepository.findById(id);
    }

    @Override
    public void delete(Cliente cliente){
        this.clienteRepository.delete(cliente);
    }


}
