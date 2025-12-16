package com.bluemoonproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {//
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTS(1002, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(1005, "User not exists", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1009, "Role not found", HttpStatus.NOT_FOUND), // Added ROLE_NOT_FOUND
    EMAIL_EXISTS(1010,"Email has already been used", HttpStatus.BAD_REQUEST),
    ROOM_EXISTS(1011,"Room has already existed", HttpStatus.BAD_REQUEST),

    //validate
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be 9 chars at least, at least one capital, at least one special char", HttpStatus.BAD_REQUEST),
    INVALID_DOB(1008, "Must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1030, "Invalid input", HttpStatus.BAD_REQUEST),

    //dob
    INVALID_DATE_FORMAT(400, "Ngày không đúng định dạng. Vui lòng dùng yyyy-MM-dd.", HttpStatus.BAD_REQUEST),

    //Request không đúng định dạng
    BAD_REQUEST(400, "Yêu cầu không hợp lệ.",  HttpStatus.BAD_REQUEST),

    //Enum không hợp lệ
    INVALID_ENUM_VALUE(1003, "Giá trị enum không hợp lệ", HttpStatus.BAD_REQUEST),

    //Room
    ROOM_NOT_FOUND(1012, "Room not found", HttpStatus.NOT_FOUND),
    RESIDENT_NOT_FOUND(1013, "Resident not found in this room", HttpStatus.NOT_FOUND),
    DUPLICATE_RESIDENT(1014, "Resident already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_ROOM(1015, "User not assigned to this room", HttpStatus.BAD_REQUEST),


    //Fee
    INVALID_EXCEL_DATA(1022, "Excel data invalid", HttpStatus.BAD_REQUEST),
    EXCEL_READ_ERROR(1023, "Unable to read Excel file", HttpStatus.INTERNAL_SERVER_ERROR),
    FEE_NOT_FOUND(1024, "Fee not found", HttpStatus.NOT_FOUND),
    DB_ERROR(1025, "Database error", HttpStatus.INTERNAL_SERVER_ERROR),


    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
