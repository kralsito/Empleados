package com.gestion.empleados.domains.auth.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum AuthError implements ErrorCode {
    AUTH_ERROR("001", "AUTH_ERROR","Error al iniciar sesión");

    private final String code;

    private final String model;

    private final String message;

    AuthError(String code, String model, String message) {
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