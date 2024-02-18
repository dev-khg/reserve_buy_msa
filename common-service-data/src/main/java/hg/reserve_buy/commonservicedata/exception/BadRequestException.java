package hg.reserve_buy.commonservicedata.exception;

public class BadRequestException extends BaseRuntimeException {
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }
}
