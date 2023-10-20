package com.inetum.demo.domain;

import com.inetum.demo.dtos.AlumnoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "Alumno")
@Table(name = "ALUMNO")
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Alumno.searchByNamedQueryName",
                query = "SELECT a FROM Alumno a WHERE a.nombre = :name"
        )
})
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Size(min = 3, max = 20, message = "el nombre debe tener mas de 3 letras y menos de 20.")
    @Column(name = "name")
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
