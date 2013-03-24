package org.metricminer.model;

import javax.persistence.Embeddable;

import org.hibernate.annotations.Type;

@Embeddable
public class QueryResultStatus {
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAILED_STATUS = "FAILED";
    @Type(type = "text")
    private String message;
    private String status;

    public QueryResultStatus() {
    }

    public static QueryResultStatus FAILED(String message) {
        return new QueryResultStatus(FAILED_STATUS, message);
    }
    
    public static QueryResultStatus SUCCESS() {
        return new QueryResultStatus(SUCCESS_STATUS);
    }
    
    private QueryResultStatus(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    
    public QueryResultStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    
    public QueryResultStatus withMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isFail() {
        return status.equals(FAILED_STATUS);
    }
    
    @Override
    public String toString() {
        return status;
    }
}
