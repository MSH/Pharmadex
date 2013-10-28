package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum PaymentStatus {
    INVOICE_ISSUED,
    PAID,
    PAYMENT_VERIFIED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
