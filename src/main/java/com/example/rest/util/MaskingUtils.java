package com.example.rest.util;

public final class MaskingUtils {
    private MaskingUtils() {}

    public static String maskSSN(String ssn) {
        if (ssn == null) return null;
        // remove non-digits
        String digits = ssn.replaceAll("\\D", "");
        if (digits.length() <= 4) return "****";
        String last4 = digits.substring(digits.length() - 4);
        return "****-****-" + last4; // or format as ***-**-1234 per your rules
    }
}
