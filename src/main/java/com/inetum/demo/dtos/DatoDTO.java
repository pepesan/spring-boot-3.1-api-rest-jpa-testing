package com.inetum.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatoDTO {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 20, message = "Debe tener entre 4 y 20 chars")
    private String cadena;
}
