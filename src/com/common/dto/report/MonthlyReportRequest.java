package com.common.dto.report;

public class MonthlyReportRequest {

    private final int requesterId;
    private final int year;
    private final int month;

    public MonthlyReportRequest(int requesterId) {
        this.requesterId = requesterId;
        java.time.LocalDate now = java.time.LocalDate.now();
        this.year = now.getYear();
        this.month = now.getMonthValue();
    }

    public MonthlyReportRequest(int requesterId, int year, int month) {
        this.requesterId = requesterId;
        this.year = year;
        this.month = month;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return "MonthlyReportRequest{requesterId=" + requesterId +
                ", year=" + year + ", month=" + month + "}";
    }
}