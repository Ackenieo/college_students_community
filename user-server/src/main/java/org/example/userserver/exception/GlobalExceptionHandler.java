package org.example.userserver.exception;

import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常: ", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误: " + e.getMessage());
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数异常: ", e);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "参数错误: " + e.getMessage());
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public Result<Object> handleIllegalStateException(IllegalStateException e) {
        logger.warn("状态异常: ", e);
        return Result.error(ResultCode.BAD_REQUEST.getCode(), "状态错误: " + e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("参数验证失败: {}", errors);
        Result<Map<String, String>> result = new Result<>();
        result.setCode(ResultCode.BAD_REQUEST.getCode());
        result.setMessage("参数验证失败");
        result.setData(errors);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    @ExceptionHandler(BindException.class)
    public Result<Map<String, String>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("绑定异常: {}", errors);
        Result<Map<String, String>> result = new Result<>();
        result.setCode(ResultCode.BAD_REQUEST.getCode());
        result.setMessage("数据绑定失败");
        result.setData(errors);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    @ExceptionHandler(Exception.class)
    public Result<Object> handleGenericException(Exception e) {
        logger.error("未知异常: ", e);
        return Result.error(ResultCode.INTERNAL_SERVER_ERROR.getCode(), "系统内部错误");
    }
}
