package com.gestion.empleados.domains.payment.error;

import com.gestion.empleados.shared.exception.error.ErrorCode;

public enum PaymentError implements ErrorCode {
    NOT_FOUND("001", "PAYMENT", "No se encontró el pago"),
    ALREADY_EXISTS("002", "PAYMENT", "Ya existe un pago para ese empleado en ese período"),
    ALREADY_PAID("003", "PAYMENT", "El pago ya fue realizado y no puede modificarse"),
    NO_WORKLOGS("004", "PAYMENT", "No hay registros de horas para ese empleado en ese período");

    private final String code;
    private final String model;
    private final String message;

    PaymentError(String code, String model, String message) {
        this.code = code;
        this.model = model;
        this.message = message;
    }

    @Override
    public String getMessage() { return message; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getModel() { return model; }
}
