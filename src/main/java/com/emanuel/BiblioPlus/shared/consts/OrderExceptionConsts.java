package com.emanuel.BiblioPlus.shared.consts;

public class OrderExceptionConsts {
    public static final String USER_ALREADY_HAVE_ORDER_IN_THIS_PERIOD = "This user already have a book reserved in this interval";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_START_DATE_CANNOT_AFTER_END = "Order start date field cannot be after end date";
    public static final String ORDER_END_DATE_CANNOT_BEFORE_START = "Order end date cannot be before start date";
    public static final String ORDER_SHOULD_NOT_CHANGE_USER = "Should not be change the user of the order";
    public static final String BOOK_OUT_OF_STOCK = "The book that you trying to access is out of stock";
    public static final String USER_HAVE_ORDER_PENDENT_TO_RETURN = "This user have do an order of a book and don't return the book";
    public static final String ORDER_START_DATE_CANNOT_BE_IN_PAST = "Order start date cannot be in the past.";
    public static final String ORDER_END_DATE_CANNOT_BE_IN_PAST = "Order end date cannot be in the past.";
}
