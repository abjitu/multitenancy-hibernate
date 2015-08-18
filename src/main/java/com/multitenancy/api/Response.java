package com.multitenancy.api;

public class Response {
    private final ResultType status;

    private final String message;

    public ResultType getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public enum ResultType {
        SUCCESS,
        FAILURE
    }

    private Response(ResultType status, String message) {
        this.message = message;
        this.status = status;
    }

    public static Response get(ResultType status) {
        return new Response(status, null);
    }

    public static Response get(ResultType status, String msg) {
        return new Response(status, msg);
    }

}
