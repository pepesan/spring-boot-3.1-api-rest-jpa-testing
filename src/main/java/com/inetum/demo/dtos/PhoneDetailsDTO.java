package com.inetum.demo.dtos;

import com.inetum.demo.domain.onetoone.PhoneDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhoneDetailsDTO {
    @NotBlank
    @Size(max = 50, message = "el proveedor debe tener 50 letras como máximo.")
    private String provider;
    private String technology;

    public static PhoneDetailsDTO from(PhoneDetails details) {
        PhoneDetailsDTO dto = new PhoneDetailsDTO();
        dto.setProvider(details.getProvider());
        dto.setTechnology(details.getTechnology());
        return dto;
    }
}
