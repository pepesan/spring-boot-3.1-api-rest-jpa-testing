package com.inetum.demo.dtos;

import com.inetum.demo.validations.NotEqual;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidadoDTO {
    @NotEqual(value = "admin", message = "El nombre de usuario no puede ser 'admin'")
    private String username;
}
