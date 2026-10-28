package dev.xichen.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一错误响应，按设计文档 §3.2 输出 RFC 7807 ProblemDetail。
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final String TYPE_BASE = "https://life-demo.dev/errors/";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException ex, HttpServletRequest request) {
        return problem(ex.getStatus(), ex.getMessage(), request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {
        List<Map<String, Object>> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.add(Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage() == null ? "不合法" : error.getDefaultMessage())));
        String detail = errors.isEmpty() ? "请求参数校验失败" : errors.get(0).get("field") + " " + errors.get(0).get("message");
        return problem(400, detail, request.getRequestURI(), errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegal(IllegalArgumentException ex, HttpServletRequest request) {
        return problem(400, ex.getMessage() == null ? "请求参数不合法" : ex.getMessage(),
                request.getRequestURI(), List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex,
                                                               HttpServletRequest request) {
        return problem(400, "请求体格式不正确", request.getRequestURI(), List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                 HttpServletRequest request) {
        return problem(400, "参数 " + ex.getName() + " 格式不正确", request.getRequestURI(), List.of());
    }

    private ResponseEntity<Map<String, Object>> problem(int status, String detail, String instance,
                                                        List<Map<String, Object>> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", TYPE_BASE + slug(status));
        body.put("title", title(status));
        body.put("status", status);
        body.put("detail", detail);
        body.put("instance", instance);
        if (!errors.isEmpty()) {
            body.put("errors", errors);
        }
        body.put("timestamp", OffsetDateTime.now().toString());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

    private String title(int status) {
        return switch (status) {
            case 400 -> "请求参数校验失败";
            case 403 -> "禁止访问";
            case 404 -> "资源不存在";
            case 405 -> "方法不允许";
            case 409 -> "数据冲突";
            case 503 -> "服务暂不可用";
            default -> status >= 500 ? "服务内部错误" : "请求失败";
        };
    }

    private String slug(int status) {
        return switch (status) {
            case 400 -> "validation";
            case 403 -> "forbidden";
            case 404 -> "not-found";
            case 409 -> "conflict";
            case 503 -> "unavailable";
            default -> "server";
        };
    }
}
