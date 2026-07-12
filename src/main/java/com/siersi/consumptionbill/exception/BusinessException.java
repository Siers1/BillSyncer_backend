package com.siersi.consumptionbill.exception;

import com.siersi.consumptionbill.enums.BusinessExceptionEnum;
import lombok.Data;
import lombok.Getter;

/**
 * 业务异常类
 * 用于封装业务逻辑中的异常情况，包含错误码和错误信息
 * 继承自RuntimeException，可以携带自定义的错误码
 * 
 * @author siersi
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(BusinessExceptionEnum businessExceptionEnum) {
        super(businessExceptionEnum.getMessage());
        this.code = businessExceptionEnum.getCode();
    }
}
