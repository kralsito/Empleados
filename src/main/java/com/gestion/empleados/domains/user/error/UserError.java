package com.gestion.empleados.domains.user.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum UserError implements ErrorCode {
    USER_NOT_LOGIN("001", "USER", "No se encontro ningun usuario logueado"),
    USER_NOT_FOUND("002", "USER", "No se encontro el usuario"),
    EMAIL_ALREADY_EXISTS("003", "USER", "Ya existe un usuario con ese email");

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
