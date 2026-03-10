package com.gestion.empleados.domains.worklog.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum WorkLogError implements ErrorCode {
    WORKLOG_NOT_FOUND("001", "WORKLOG", "No se encontró el registro"),
    ALREADY_EXISTS("002", "WORKLOG", "Ya existe un registro para ese empleado en esa fecha");

    private final String code;

    private final String model;

    private final String message;

    WorkLogError(String code, String model, String message) {
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
