package centrikt.factorymonitoring.authserver.exceptions;

public class MethodDisabledException extends RuntimeException {
    public MethodDisabledException(String message) {
        super(message);
    }
}
