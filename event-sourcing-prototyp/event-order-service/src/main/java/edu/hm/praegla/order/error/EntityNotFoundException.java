package edu.hm.praegla.order.error;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BrickstoreException {

    public EntityNotFoundException(Class<?> clazz, String key, Object value) {
        super(ResponseCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND, EntityNotFoundException.generateMessage(clazz.getSimpleName(), key, value));
    }

    public EntityNotFoundException(String message) {
        super(ResponseCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND, message);
    }

    private static String generateMessage(String entity, String key, Object value) {
        return String.format("%s was not found for parameter %s=%s", entity, key, value);
    }

}
