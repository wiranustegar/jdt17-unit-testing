package com.indivara.jdt17.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Employee dengan ID " + id + " tidak ditemukan");
    }
}
