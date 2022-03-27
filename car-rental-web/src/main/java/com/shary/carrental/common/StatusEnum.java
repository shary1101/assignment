package com.shary.carrental.common;

public enum StatusEnum {
    FAIL("fail"), SUCCESS("success");

    String status;

    StatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
