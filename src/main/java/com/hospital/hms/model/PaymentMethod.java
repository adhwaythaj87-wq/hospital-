package com.hospital.hms.model;

public enum PaymentMethod {
    CASH("Cash"),
    CARD("Credit/Debit Card"),
    UPI("UPI / QR Code"),
    INSURANCE("Health Insurance"),
    NET_BANKING("Net Banking");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
