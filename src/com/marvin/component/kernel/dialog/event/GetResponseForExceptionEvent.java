package com.marvin.component.kernel.dialog.event;

import com.marvin.component.kernel.dialog.Request;
import com.marvin.component.kernel.dialog.RequestHandler;
import com.marvin.component.kernel.dialog.Response;

public class GetResponseForExceptionEvent extends GetResponseEvent {
    
    private Exception exception;

    public GetResponseForExceptionEvent(RequestHandler handler, Request request, Exception exception) {
        super(handler, request);
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}