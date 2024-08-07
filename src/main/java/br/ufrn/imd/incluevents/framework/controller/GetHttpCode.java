package br.ufrn.imd.incluevents.framework.controller;

import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;

public class GetHttpCode {

    public static int getHttpCode(ExceptionTypesEnum exceptionType) {
        switch (exceptionType) {
            case NOT_FOUND:
                return org.springframework.http.HttpStatus.NOT_FOUND.value();
            case BAD_REQUEST:
                return org.springframework.http.HttpStatus.BAD_REQUEST.value();
            case CONFLICT:
                return org.springframework.http.HttpStatus.CONFLICT.value();
            case FORBIDDEN:
                return org.springframework.http.HttpStatus.FORBIDDEN.value();
            default:
                return org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }

}
