package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.DashboardStats;
import com.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStats> getStats() {

        return ApiResponse.success(dashboardService.getStats());
    }
}
