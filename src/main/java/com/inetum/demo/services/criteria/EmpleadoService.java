package com.inetum.demo.services.criteria;

import com.inetum.demo.domain.herencia.Empleado;
import com.inetum.demo.repositories.herencia.EmpleadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {


    private final EmpleadoRepository empleadoRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<Empleado> buscarEmpleadosConCriteria(String nombre, Integer edad) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Empleado> query = cb.createQuery(Empleado.class);
        Root<Empleado> empleado = query.from(Empleado.class);

        // Filtrar por nombre y edad
        if (nombre != null) {
            query.select(empleado).where(cb.equal(empleado.get("nombre"), nombre));
        }
        if (edad != null) {
            query.select(empleado).where(cb.equal(empleado.get("edad"), edad));
        }

        return entityManager.createQuery(query).getResultList();
    }

}
