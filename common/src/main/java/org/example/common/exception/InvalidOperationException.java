package org.example.common.exception;

import lombok.Data;
import org.example.common.result.ResultCode;

@Data
public class InvalidOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public InvalidOperationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public InvalidOperationException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public InvalidOperationException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    public InvalidOperationException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }
}
