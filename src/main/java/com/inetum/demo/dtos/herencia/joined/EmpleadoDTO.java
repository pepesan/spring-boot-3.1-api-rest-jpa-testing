package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Empleado;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmpleadoDTO extends EmpleadoBaseDTO<Empleado> {
}
