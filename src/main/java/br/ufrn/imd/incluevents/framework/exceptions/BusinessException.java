package br.ufrn.imd.incluevents.framework.exceptions;

import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;

public class BusinessException extends Exception {

    public ExceptionTypesEnum type;

    public BusinessException(String message, ExceptionTypesEnum type) {
        super(message);
        this.type = type;
    }

    public ExceptionTypesEnum getType() {
        return type;
    }
}
