package com.IOA.dto;

public class NormalMessage {
    private boolean success;
    private String error;
    private Object message;


    public NormalMessage() {
    }

    public NormalMessage(boolean success, String error, Object message) {
        this.success = success;
        this.error = error;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
