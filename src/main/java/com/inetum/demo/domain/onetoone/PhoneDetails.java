package com.inetum.demo.domain.onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "PhoneDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDetails {
    @Id
    @GeneratedValue
    private Long id;
    private String provider;
    private String technology;
}
