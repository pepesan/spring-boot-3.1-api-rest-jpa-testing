package com.inetum.demo.mappers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AlumnoMapper {
    private final ModelMapper modelMapper = new ModelMapper();

    public AlumnoDTO toDto(Alumno alumno) {
        return modelMapper.map(alumno, AlumnoDTO.class);
    }

    public Alumno toEntity(AlumnoDTO dto) {
        return modelMapper.map(dto, Alumno.class);
    }
}

