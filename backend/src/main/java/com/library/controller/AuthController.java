package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.LoginRequest;
import com.library.dto.LoginResponse;
import com.library.entity.Admin;
import com.library.mapper.AdminMapper;
import com.library.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        Admin admin = adminMapper.findByUsername(request.getUsername()).orElse(null);

        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(admin.getUsername());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .name(admin.getName())
                .role(admin.getRole())
                .avatar(admin.getAvatar())
                .build();

        return ApiResponse.success("登录成功", response);
    }
}
