package com.bluemoonproject.controller;

import com.bluemoonproject.dto.request.*;

import com.bluemoonproject.dto.response.ApiResponse;
import com.bluemoonproject.dto.response.AuthenticationResponse;
import com.bluemoonproject.dto.response.IntrospectResponse;
import com.bluemoonproject.service.AuthenticationService;
import com.bluemoonproject.service.OtpService;
import com.bluemoonproject.service.UserService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    final AuthenticationService authenticationService;
    final OtpService otpService;
    final UserService userService;
    //
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request) {
        var result= authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result)
                .build();
    }

    //endpoint: .../auth/introspect
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticated(@RequestBody IntrospectRequest request)   throws ParseException, JOSEException  {
        var result= authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result)
                .build();
    }

    //endpoint:.../auth/logout
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    //endpoint:.../auth/refresh
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        var result= authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result)
                .build();
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP verified! You can now reset your password.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {

        String response = userService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok(response);
    }

}
