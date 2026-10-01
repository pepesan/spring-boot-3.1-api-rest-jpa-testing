package com.inetum.demo.dtos;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.inetum.demo.domain.onetoone.Phone;
import com.inetum.demo.domain.onetoone.PhoneDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/** Datos de entrada de un Phone (POST/PUT y base del PATCH), con su PhoneDetails anidado. */
@Data
public class PhoneDTO {
    @NotBlank
    @Pattern(regexp = "\\d{9}", message = "el número debe tener 9 dígitos.")
    private String number;

    /**
     * Opcional. {@code @JsonMerge}: en un PATCH un objeto parcial ({"details": {"technology": "4G"}})
     * se fusiona con los details actuales en vez de reemplazarlos; {@code "details": null} los elimina.
     */
    @Valid
    @JsonMerge
    private PhoneDetailsDTO details;

    public static PhoneDTO from(Phone phone) {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(phone.getNumber());
        if (phone.getDetails() != null) {
            dto.setDetails(PhoneDetailsDTO.from(phone.getDetails()));
        }
        return dto;
    }

    /**
     * Vuelca este DTO sobre la entidad. Los details existentes se actualizan (conservan su id);
     * si el DTO no trae details, se eliminan los de la entidad (orphanRemoval).
     */
    public void applyTo(Phone phone) {
        phone.setNumber(number);
        if (details == null) {
            phone.setDetails(null);
            return;
        }
        PhoneDetails entity = phone.getDetails() != null ? phone.getDetails() : new PhoneDetails();
        entity.setProvider(details.getProvider());
        entity.setTechnology(details.getTechnology());
        phone.setDetails(entity);
    }
}
