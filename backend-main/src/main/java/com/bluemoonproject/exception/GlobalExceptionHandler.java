package com.bluemoonproject.exception;

import com.bluemoonproject.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";


    // Xử lí exception RuntimeException chưa được xử lỹ rõ ràng
    @ExceptionHandler(value = RuntimeException.class) // Chỉ xử lý RuntimeException
    public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Xử lí các Exception tự định nghĩa
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    //Xử lí các lỗi khi user không có quyền truy cập (do @PreAuthorize hay security)
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    // Bắt lỗi khi validate thất bại (thường dùng @Valid trong request body)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Lấy FieldError đầu tiên (nếu có)
        FieldError fieldError = ex.getBindingResult().getFieldError();

        // Mặc định fallback
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        String message = "Invalid request";

        if (fieldError != null) {
            String defaultMessage = fieldError.getDefaultMessage(); // thường là enum key bạn set trong @NotNull/@Size…
            try {
                // Thử chuyển defaultMessage thành enum
                errorCode = ErrorCode.valueOf(defaultMessage);
                message = errorCode.getMessage();
            } catch (IllegalArgumentException e) {
                // Nếu không phải key của enum, ghi log và dùng thông điệp gốc
                log.error("Hãy thử lại!", defaultMessage, e);
                message = defaultMessage;
            }
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiResponse);
    }

    @ExceptionHandler(Exception.class) // Xử lý ngoại lệ chung
    public ResponseEntity<ApiResponse> handleGlobalException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage("Something went wrong: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    private String mapAttributes(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

    //Xử lí request sai định dạng
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        //Xử lí exception
        if (cause instanceof InvalidFormatException ife) {
            String field = ife.getPath().get(0).getFieldName();
            Class<?> targetType = ife.getTargetType();

            String message;
            ErrorCode errorCode;

            //Sai định dạng ngày (LocalDate)
            if (targetType.equals(LocalDate.class)) {
                message = String.format("Trường '%s' không đúng định dạng ngày. Vui lòng dùng yyyy-MM-dd.", field);
                errorCode = ErrorCode.INVALID_DATE_FORMAT;
                //Chọn không đúng enum
            } else if (targetType.isEnum()) {
                message = String.format("Trường '%s' không hợp lệ. Giá trị phải là một trong các giá trị enum hợp lệ.", field);
                errorCode = ErrorCode.INVALID_ENUM_VALUE;
            } else {
                message = String.format("Trường '%s' có giá trị không hợp lệ.", field);
                errorCode = ErrorCode.BAD_REQUEST;
            }

            return ResponseEntity.status(errorCode.getStatusCode()).body(
                    ApiResponse.builder()
                            .code(errorCode.getCode())
                            .message(message)
                            .build()
            );
        }

        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }
}
