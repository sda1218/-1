package com.example.drone.exception;

import com.example.drone.domain.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器.
 * <p>
 * 统一处理系统中的所有异常，将异常转换为标准的错误响应格式。
 * 使用 @RestControllerAdvice 注解，自动应用于所有 @RestController。
 * </p>
 * 
 * <p>处理的异常类型：
 * <ul>
 *   <li>DroneNotFoundException - 无人机不存在（404）</li>
 *   <li>DuplicateSerialNumberException - 序列号重复（400）</li>
 *   <li>MethodArgumentNotValidException - 参数校验失败（400）</li>
 *   <li>UnauthorizedException - 未授权（401）</li>
 *   <li>ForbiddenException - 禁止访问（403）</li>
 *   <li>Exception - 其他未处理异常（500）</li>
 * </ul>
 * </p>
 *
 * @author 开发团队
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理无人机不存在异常.
     * <p>
     * 当查询、更新或删除不存在的无人机时触发。
     * 返回 HTTP 404 状态码。
     * </p>
     *
     * @param ex 无人机不存在异常
     * @return 错误响应（404）
     */
    @ExceptionHandler(DroneNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDroneNotFound(DroneNotFoundException ex) {
        log.warn("无人机不存在: {}", ex.getMessage());
        return ResponseEntity.status(404)
                .body(ErrorResponse.of(404, ex.getMessage()));
    }

    /**
     * 处理序列号重复异常.
     * <p>
     * 当创建或更新无人机时，序列号与已有记录冲突时触发。
     * 返回 HTTP 400 状态码。
     * </p>
     *
     * @param ex 序列号重复异常
     * @return 错误响应（400）
     */
    @ExceptionHandler(DuplicateSerialNumberException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateSerialNumber(DuplicateSerialNumberException ex) {
        log.warn("序列号重复: {}", ex.getMessage());
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, ex.getMessage()));
    }

    /**
     * 处理参数校验失败异常.
     * <p>
     * 当请求参数不符合 @Valid 注解定义的校验规则时触发。
     * 返回 HTTP 400 状态码，并包含具体的字段验证错误信息。
     * </p>
     *
     * @param ex 参数校验异常
     * @return 错误响应（400），包含字段错误详情
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.warn("参数校验失败: {}", errors);
        return ResponseEntity.status(400)
                .body(ErrorResponse.of(400, "参数校验失败", errors));
    }

    /**
     * 处理未授权异常.
     * <p>
     * 当用户未通过身份认证就访问需要认证的资源时触发。
     * 返回 HTTP 401 状态码。
     * </p>
     *
     * @param ex 未授权异常
     * @return 错误响应（401）
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        log.warn("未授权访问: {}", ex.getMessage());
        return ResponseEntity.status(401)
                .body(ErrorResponse.of(401, ex.getMessage()));
    }

    /**
     * 处理禁止访问异常.
     * <p>
     * 当用户已认证但权限不足时触发。
     * 返回 HTTP 403 状态码。
     * </p>
     *
     * @param ex 禁止访问异常
     * @return 错误响应（403）
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        log.warn("权限不足: {}", ex.getMessage());
        return ResponseEntity.status(403)
                .body(ErrorResponse.of(403, ex.getMessage()));
    }

    /**
     * 处理其他未捕获的异常.
     * <p>
     * 作为兜底处理器，捕获所有未被其他处理器处理的异常。
     * 返回 HTTP 500 状态码，并记录详细的错误日志。
     * </p>
     *
     * @param ex 未处理的异常
     * @return 错误响应（500）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("系统内部错误", ex);
        return ResponseEntity.status(500)
                .body(ErrorResponse.of(500, "系统内部错误"));
    }
}
