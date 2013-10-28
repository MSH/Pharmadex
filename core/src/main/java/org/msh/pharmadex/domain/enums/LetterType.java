package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum LetterType {
    ACK_SUBMITTED,
    ACK_RECEIVED,
    INVOICE,
    PAYMENT_RECEIVED,
    REMINDER;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
