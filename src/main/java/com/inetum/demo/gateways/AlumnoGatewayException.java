package com.inetum.demo.gateways;

/**
 * Único error que el gateway deja salir hacia el resto de la aplicación: oculta las excepciones
 * concretas del cliente HTTP y de los reintentos.
 *
 * @param transitorio true si el fallo es del servicio remoto (red, timeout, 5xx) y puede
 *                    resolverse reintentando más tarde; false si el remoto rechazó la petición (4xx).
 */
public class AlumnoGatewayException extends RuntimeException {

    private final boolean transitorio;

    public AlumnoGatewayException(String message, Throwable cause, boolean transitorio) {
        super(message, cause);
        this.transitorio = transitorio;
    }

    public boolean isTransitorio() {
        return transitorio;
    }
}
