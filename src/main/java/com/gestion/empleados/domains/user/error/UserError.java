package com.gestion.empleados.domains.user.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum UserError implements ErrorCode {
    USER_NOT_LOGIN("001", "USER","No se encontró ningun usuario logueado");

    private final String code;

    private final String model;

    private final String message;

    UserError(String code, String model, String message) {
        this.code = code;
        this.model = model;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getModel() {
        return model;
    }
}

