package com.gestion.empleados.domains.role.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum RoleError implements ErrorCode {
    ROLE_NOT_FOUND("001", "ROLE","No se encontró el rol"),
    HAS_EMPLOYEES("002", "ROLE", "No se puede eliminar el rol porque tiene empleados asociados")
;

    private final String code;

    private final String model;

    private final String message;

    RoleError(String code, String model, String message) {
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
