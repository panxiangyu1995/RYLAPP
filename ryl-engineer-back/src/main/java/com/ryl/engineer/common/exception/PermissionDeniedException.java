package com.ryl.engineer.common.exception;
 
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException(String message) {
        super(message);
    }
} 