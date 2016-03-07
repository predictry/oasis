package com.predictry.oasis.domain.exception;

/**
 * This exception is raised whenever there is not enough data to generate recommendation email.
 * It may be caused by invalid product id, no recommendation for that product, etc.
 *
 */
public class TemplateDataNotFoundException extends RuntimeException {

    public TemplateDataNotFoundException(String message) {
        super(message);
    }

}
