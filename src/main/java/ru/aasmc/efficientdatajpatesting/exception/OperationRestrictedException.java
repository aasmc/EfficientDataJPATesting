package ru.aasmc.efficientdatajpatesting.exception;

public class OperationRestrictedException extends RuntimeException {
    public OperationRestrictedException(String message) {
        super(message);
    }
}
