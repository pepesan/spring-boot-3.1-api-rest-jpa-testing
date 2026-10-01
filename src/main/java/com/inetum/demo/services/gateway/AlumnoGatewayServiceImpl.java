package com.inetum.demo.services.gateway;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.gateways.AlumnoGateway;
import com.inetum.demo.gateways.AlumnoGatewayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AlumnoGatewayServiceImpl implements AlumnoGatewayService {

    private final AlumnoGateway gateway;
    /** Última lista obtenida con éxito: el fallback de findAll cuando el remoto no responde. */
    private volatile List<Alumno> ultimoListado;

    public AlumnoGatewayServiceImpl(AlumnoGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public Listado findAll() {
        try {
            List<Alumno> actual = gateway.findAll();
            ultimoListado = actual;
            return new Listado(actual, false);
        } catch (AlumnoGatewayException e) {
            List<Alumno> respaldo = ultimoListado;
            if (e.isTransitorio() && respaldo != null) {
                log.warn("GATEWAY: servicio remoto no disponible, se devuelve el último listado conocido");
                return new Listado(respaldo, true);
            }
            throw e;
        }
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return gateway.findById(id);
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        return gateway.create(alumno);
    }

    @Override
    public Optional<Alumno> remove(Long id) {
        return gateway.delete(id);
    }
}
