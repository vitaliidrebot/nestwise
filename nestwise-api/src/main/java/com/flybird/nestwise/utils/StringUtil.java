package com.flybird.nestwise.utils;

public class StringUtil {
    /**
     * Mask IBAN, leaving the first 4 characters (country code + checksum)
     * and the last 4 digits visible, masking everything in between
     *
     * @param iban IBAN number (up to 34 alphanumeric characters, 29 for Ukraine)
     * @return masked String
     */
    public static String maskIBAN(String iban) {
        if (iban == null || iban.length() != 29) {
            throw new IllegalArgumentException("Invalid IBAN");
        }

        return maskString(iban, 4, 4);
    }

    /**
     * Mask credit card number, leaving the last 4 digits visible
     *
     * @param creditCard credit card number (16 digits)
     * @return masked String
     */
    public static String maskCreditCard(String creditCard) {
        if (creditCard == null || creditCard.length() != 16) {
            throw new IllegalArgumentException("Invalid credit card number");
        }

        return maskString(creditCard, 0, 4);
    }

    public static String maskString(String plainText, int startIndex, int fromEndIndex) {
        if (plainText == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        }
        if (startIndex < 0 || fromEndIndex < 0) {
            throw new IllegalArgumentException("Indices cannot be negative");
        }
        if (startIndex + fromEndIndex > plainText.length()) {
            throw new IllegalArgumentException("Indices are out of bounds");
        }

        int endIndex = plainText.length() - fromEndIndex;

        return plainText.substring(0, startIndex) + "*".repeat(endIndex - startIndex) + plainText.substring(endIndex);
    }
}
