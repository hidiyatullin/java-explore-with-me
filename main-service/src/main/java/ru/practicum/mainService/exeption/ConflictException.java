package ru.practicum.mainService.exeption;

public class ConflictException extends RuntimeException {

    public ConflictException(String error) {
        super(error);
    }
}