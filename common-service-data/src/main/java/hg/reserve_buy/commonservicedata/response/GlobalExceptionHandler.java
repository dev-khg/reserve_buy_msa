package hg.reserve_buy.commonservicedata.response;

import hg.reserve_buy.commonservicedata.exception.BadRequestException;
import hg.reserve_buy.commonservicedata.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static hg.reserve_buy.commonservicedata.response.ApiResponse.failure;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException e) {
        return badRequest().body(failure(e.getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerException(InternalServerException e) {
        return internalServerError().body(failure(e.getMessage()));
    }
}
