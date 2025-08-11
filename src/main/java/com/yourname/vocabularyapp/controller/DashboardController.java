package com.yourname.vocabularyapp.controller;

import com.yourname.vocabularyapp.dto.DashboardStatsDto;
import com.yourname.vocabularyapp.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Trả về trang HTML cho Bảng điều khiển
    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }

    // API Endpoint để cung cấp dữ liệu JSON cho trang dashboard
    @GetMapping("/dashboard/stats")
    @ResponseBody // Rất quan trọng: Báo cho Spring biết đây là API trả về JSON, không phải tên template
    public ResponseEntity<DashboardStatsDto> getDashboardStats(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        DashboardStatsDto stats = dashboardService.getStatsForUser(principal.getName());
        return ResponseEntity.ok(stats);
    }
}