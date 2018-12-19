package com.qunar.cm.ic.controller;

import com.google.common.base.Strings;
import com.qunar.cm.ic.common.exception.ICException;
import com.qunar.cm.ic.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * Created by yu.qi on 2018/08/31.
 */
public abstract class AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(AbstractController.class);

    @Resource
    private HttpServletRequest request;

    String getClientIp() {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (Strings.isNullOrEmpty(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /* 异常处理，输出异常信息 */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponse> handlerRuntimeException(RuntimeException e) {
        logger.error("Controller运行时异常：{}", e.getMessage(), e);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        MessageResponse messageResponse = new MessageResponse("系统异常");
        //使用@Validated注解对参数进行校验的时候，如果失败则会抛出ConstraintViolationException这个异常
        if (e instanceof ICException || e instanceof ConstraintViolationException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            messageResponse = new MessageResponse(e.getMessage());
        }
        return new ResponseEntity<>(messageResponse, httpStatus);
    }

}
