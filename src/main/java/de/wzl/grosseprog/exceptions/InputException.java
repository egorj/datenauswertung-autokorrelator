package de.wzl.grosseprog.exceptions;

import java.io.IOException;

public class InputException extends IOException {

    public InputException() {
    }

    public InputException(String message) {
        super(message);
    }

    public InputException(Throwable cause) {
        super(cause);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }
}
