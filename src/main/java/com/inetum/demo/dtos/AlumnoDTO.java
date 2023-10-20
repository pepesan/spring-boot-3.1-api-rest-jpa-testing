package com.inetum.demo.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlumnoDTO {
    @Size(min = 3, max = 20, message = "el nombre debe tener mas de 3 letras y menos de 20.")
    private String nombre;
    private String apellidos;
    @Min(value = 18, message = "el usuario debe tener 18+")
    private Integer edad;
}
