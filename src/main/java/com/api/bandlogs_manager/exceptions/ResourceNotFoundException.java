package com.api.bandlogs_manager.exceptions;

/**
 * Project: bandlogs-manager
 * Description: This Exception manages the researches over objects not existents in database
 * @author  Brando Eli Carrillo Perez
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Error en consulta: objeto no hallado con parámetro de solicitud");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
