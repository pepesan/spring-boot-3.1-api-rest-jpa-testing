package com.inetum.demo.mappers;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AlumnoMapper {
    AlumnoMapper INSTANCE = Mappers.getMapper(AlumnoMapper.class);

    AlumnoDTO toDto(Alumno alumno);
    Alumno toEntity(AlumnoDTO alumnoDTO);
}
