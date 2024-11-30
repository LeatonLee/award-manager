package com.example.exception;

import com.example.pojo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 */
public class GlobalExceptionHandle {
    @ExceptionHandler(Exception.class)//捕获所有异常
    public Result ex(Exception ex){
        ex.printStackTrace();
        return Result.error("对不起，操作失败，请联系管理员");
    }
}

