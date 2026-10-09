package com.hospital.hms.model;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
