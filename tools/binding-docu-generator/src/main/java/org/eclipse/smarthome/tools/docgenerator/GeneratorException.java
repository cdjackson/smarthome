package org.eclipse.smarthome.tools.docgenerator;

public class GeneratorException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -5410328061331045693L;

    public GeneratorException() {
        super();
    }

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneratorException(Throwable cause) {
        super(cause);
    }

    protected GeneratorException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
