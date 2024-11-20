package com.inetum.demo.ejercicios;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cliente {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String dir;

    public Cliente(ClienteDTO clienteDTO){
        this.id = 0L;
        this.name = clienteDTO.getName();
        this.email = clienteDTO.getEmail();
        this.dir = clienteDTO.getDir();
    }
}
