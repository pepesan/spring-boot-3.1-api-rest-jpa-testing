package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Jefe;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JefeDTO extends EmpleadoBaseDTO<Jefe> {
    @NotBlank
    private String departamento;

    @Override
    public void applyTo(Jefe jefe) {
        super.applyTo(jefe);
        jefe.setDepartamento(departamento);
    }
}
