package com.inetum.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dato implements Serializable {
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 4, max = 20, message = "Debe tener entre 1 y 100 chars")
    private String cadena;
    public Dato(DatoDTO datoDTO) {
        this.cadena = datoDTO.getCadena();
    }
}
