package com.emanuel.BiblioPlus.modules.orders.infra.database.entities;

public enum OrderStatus {
    RETURNED("returned"),
    DELIVERED("delivered");

    private String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
