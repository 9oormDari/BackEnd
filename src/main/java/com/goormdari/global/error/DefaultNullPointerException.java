package com.goormdari.global.error;

import com.goormdari.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class DefaultNullPointerException extends NullPointerException {

    private ErrorCode errorCode;
    public DefaultNullPointerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
