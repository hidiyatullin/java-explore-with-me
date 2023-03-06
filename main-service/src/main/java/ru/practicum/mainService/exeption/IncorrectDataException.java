package ru.practicum.mainService.exeption;

public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String s) {
        super(s);
    }
}
