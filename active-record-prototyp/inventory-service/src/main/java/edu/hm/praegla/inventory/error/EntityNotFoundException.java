package edu.hm.praegla.inventory.error;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BrickstoreException {

    public EntityNotFoundException(Class<?> clazz, String key, Object value) {
        super(ResponseCode.ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND, EntityNotFoundException.generateMessage(clazz.getSimpleName(), key, value));
    }

    private static String generateMessage(String entity, String key, Object value) {
        return String.format("%s was not found for parameter %s=%s", entity, key, value);
    }

}
