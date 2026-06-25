package com.indivara.jdt17.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email " + email + " sudah terdaftar");
    }
}
