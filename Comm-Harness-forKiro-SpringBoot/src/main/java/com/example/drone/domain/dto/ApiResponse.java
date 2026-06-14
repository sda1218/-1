package com.example.drone.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应格式.
 * <p>
 * 用于封装所有 API 的成功响应，包含状态码、消息和数据。
 * </p>
 *
 * @param <T> 响应数据类型
 * @author 开发团队
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应状态码.
     * <p>
     * 通常与 HTTP 状态码一致，例如：200（成功）、201（创建成功）。
     * </p>
     */
    private int code;

    /**
     * 响应消息.
     * <p>
     * 简短的响应描述，例如："success"、"创建成功"。
     * </p>
     */
    private String message;

    /**
     * 响应数据.
     * <p>
     * 实际的业务数据，可以是单个对象、列表或分页对象。
     * </p>
     */
    private T data;

    /**
     * 创建成功响应（200 OK）.
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return API 响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 创建成功响应（自定义消息）.
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return API 响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 创建创建成功响应（201 Created）.
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return API 响应对象
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "创建成功", data);
    }
}
