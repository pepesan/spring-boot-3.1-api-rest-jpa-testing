package com.inetum.demo.dtos.manytomany;

import com.inetum.demo.domain.manytomany.Role;

import java.util.List;

/**
 * Vista plana de un rol con los usuarios asignados. Evita serializar la entidad
 * (ciclo Role -> users -> roles) y las referencias por id de @JsonIdentityInfo.
 */
public record RoleWithUsersDto(long id, String name, List<UserSummary> users) {

    // Copia inmutable defensiva: el record no comparte la lista recibida (SpotBugs EI_EXPOSE_REP2)
    public RoleWithUsersDto {
        users = List.copyOf(users);
    }

    // La lista ya es inmutable (List.copyOf); el accesor no expone estado modificable
    @Override
    public List<UserSummary> users() {
        return List.copyOf(users);
    }

    /** Resumen del usuario: sin sus roles, para cortar el ciclo. */
    public record UserSummary(long id, String firstName) {
    }

    /** Convierte la entidad en DTO; role.getUsers() es EAGER, no requiere transacción. */
    public static RoleWithUsersDto from(Role role) {
        return new RoleWithUsersDto(
                role.getId(),
                role.getName(),
                role.getUsers().stream()
                        .map(u -> new UserSummary(u.getId(), u.getFirstName()))
                        .toList());
    }
}
