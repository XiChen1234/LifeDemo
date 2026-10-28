package dev.xichen.backend.common;

/**
 * 业务异常，统一转换为 RFC 7807 ProblemDetail 响应。
 */
public class ApiException extends RuntimeException {
    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException notFound(String message) {
        return new ApiException(404, message);
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, message);
    }

    public int getStatus() {
        return status;
    }
}
