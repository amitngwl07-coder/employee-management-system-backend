package com.example.employeeapi.exception;

import org.springframework.http.ResponseEntity;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email){
        super("Employee email already exist: " + email);
    }
}
