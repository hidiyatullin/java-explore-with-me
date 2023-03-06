package ru.practicum.mainService.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> incorrectUserOfItem(final IncorrectDataException e) {
        log.debug("400, {}", e.getMessage());
        return Map.of(
                "error", "Ошибка с параметром user.",
                "errorMessage", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFoundObject(final NotFoundException e) {
        log.debug("404, {}", e.getMessage());
        return Map.of(
                "error", "Ошибка с параметром.",
                "errorMessage", e.getMessage()
        );
    }
}
