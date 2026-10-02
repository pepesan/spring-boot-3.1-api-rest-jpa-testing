package com.inetum.demo.dtos.herencia.tableperclass;

import com.inetum.demo.domain.herencia.tableperclass.Gato;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GatoDTO extends AnimalDTO<Gato> {
    @NotBlank
    private String colorPelo;

    private boolean indoor;

    @Override
    public void applyTo(Gato gato) {
        applyCommon(gato);
        gato.setColorPelo(colorPelo);
        gato.setIndoor(indoor);
    }
}
