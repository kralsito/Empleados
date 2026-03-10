package com.gestion.empleados.domains.employee.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum EmployeeError implements ErrorCode {
    EMPLOYEE_NOT_FOUND("001", "ROLE","No se encontró el empleado");

    private final String code;

    private final String model;

    private final String message;

    EmployeeError(String code, String model, String message) {
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

