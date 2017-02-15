package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum LetterType {
    ACK_SUBMITTED,
    ACK_RECEIVED,
    ACC_SUBMITTED,
    ACC_RECEIVED,
    INVOICE,
    PAYMENT_RECEIVED,
    REMINDER,
    NEW_USER_REGISTRATION,
    DEFICIENCY,
    SAMPLE_TEST_RESULT,
    OTHER,
    SAMPLE_REQUEST_LETTER,
    SUSP_NOTIF_LETTER,
    CANCELLATION_LETTER,
    CANCELLATION_SENDER_LETTER,
    REJECT,
    APPROVE,
    INVITATION;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
