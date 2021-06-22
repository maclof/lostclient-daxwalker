package org.lostclient.api.wrappers.walking.dax_api.api_lib.models.exceptions;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
