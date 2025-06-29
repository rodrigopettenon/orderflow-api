package com.rodrigopettenon.orderflow.exceptions;

public class ClientErrorException extends RuntimeException{

    private static final long serialVersionUID = -4463964313614675430L;

    private Object object;

    public ClientErrorException(String msg) {
        super(msg);
    }

    public ClientErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ClientErrorException(String msg, Object object){
        super(msg);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
