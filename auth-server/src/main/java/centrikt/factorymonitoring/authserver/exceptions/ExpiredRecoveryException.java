package centrikt.factorymonitoring.authserver.exceptions;

public class ExpiredRecoveryException extends RuntimeException {
    public ExpiredRecoveryException(String message) {
        super(message);
    }
}
