package com.inetum.demo.dtos.herencia.joined;

import com.inetum.demo.domain.herencia.joined.Sobremesa;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SobremesaDTO extends OrdenadorDTO<Sobremesa> {
    @NotBlank
    private String tipoTorre;

    private boolean tieneMonitor;

    @Override
    public void applyTo(Sobremesa sobremesa) {
        applyCommon(sobremesa);
        sobremesa.setTipoTorre(tipoTorre);
        sobremesa.setTieneMonitor(tieneMonitor);
    }
}
