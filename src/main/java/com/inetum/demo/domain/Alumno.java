package com.inetum.demo.domain;

import com.inetum.demo.dtos.AlumnoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "Alumnos")
@Table(name = "ALUMNOS")
@AllArgsConstructor
public class Alumno {
    @Id
    @GeneratedValue
    private Long id;
    @Size(min = 3, max = 20, message = "el nombre debe tener mas de 3 letras y menos de 20.")
    private String nombre;
    private String apellidos;
    @Min(value = 18, message = "el usuario debe tener 18+")
    private Integer edad;
    public Alumno(){
        this.id = 0L;
        this.nombre = "";
        this.apellidos = "";
        this.edad = 0;
    }

    public Alumno(AlumnoDTO alumnoDTO){
        this.nombre = alumnoDTO.getNombre();
        this.apellidos = alumnoDTO.getApellidos();
        this.edad = alumnoDTO.getEdad();
    }
}
