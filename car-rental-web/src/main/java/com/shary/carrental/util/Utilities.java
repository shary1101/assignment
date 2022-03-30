package com.shary.carrental.util;

import java.util.Date;

/**
 * Utilities for car rental services
 *
 * @author shary
 *
 */
public class Utilities {
    private Utilities() {
    }
    public static boolean isValidDates(Date fromDate, Date toDate) {
        return fromDate.before(toDate);
    }
}
