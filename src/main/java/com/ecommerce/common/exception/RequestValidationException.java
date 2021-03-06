package com.ecommerce.common.exception;

import java.util.Map;

import static com.ecommerce.common.exception.CommonErrorCode.REQUEST_VALIDATION_FAILED;

public class RequestValidationException extends AppException {
    public RequestValidationException(Map<String, Object> data) {
        super(REQUEST_VALIDATION_FAILED, data);
    }
}
