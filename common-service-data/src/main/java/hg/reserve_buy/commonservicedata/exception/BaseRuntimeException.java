package hg.reserve_buy.commonservicedata.exception;

public abstract class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException() {
    }

    public BaseRuntimeException(String message) {
        super(message);
    }
}
