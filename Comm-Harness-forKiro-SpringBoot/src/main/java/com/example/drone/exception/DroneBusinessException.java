package com.example.drone.exception;

/**
 * 无人机业务异常基类
 * 
 * <p>所有业务异常的父类，包含错误码和错误消息。
 * 继承自 RuntimeException，支持 Spring 事务回滚。
 * 
 * <p>使用场景：
 * <ul>
 *   <li>作为所有业务异常的基类</li>
 *   <li>提供统一的错误码和错误消息结构</li>
 *   <li>支持异常链传递（cause）</li>
 * </ul>
 * 
 * @author Drone Management Team
 * @version 1.0.0
 * @since 2024-01-15
 */
public class DroneBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int errorCode;

    /**
     * 构造业务异常
     * 
     * @param errorCode 错误码（HTTP 状态码）
     * @param message 错误消息
     */
    public DroneBusinessException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造业务异常（带原因）
     * 
     * @param errorCode 错误码（HTTP 状态码）
     * @param message 错误消息
     * @param cause 异常原因
     */
    public DroneBusinessException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public int getErrorCode() {
        return errorCode;
    }
}
