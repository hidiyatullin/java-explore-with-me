package ru.practicum.mainService.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundObject(final NotFoundException e) {
        log.debug("404, {}", e.getMessage());
        return Map.of(
                "error", "Ошибка с параметром.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConflict(final ConflictException cL) {
        log.error("409 {}", cL.getMessage(), cL);
        ApiError apiError = new ApiError.ApiErrorBuilder()
                .errors(List.of(cL.getClass().getName()))
                .message(cL.getLocalizedMessage())
                .reason("Integrity constraint has been violated")
                .status(HttpStatus.NOT_FOUND)
                .build();
        return new ResponseEntity<>(apiError,
                HttpStatus.CONFLICT);
    }
}
