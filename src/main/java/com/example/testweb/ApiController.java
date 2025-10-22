package com.example.testweb;

import com.example.employeesnapshot.entity.EmployeeIncremental;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API控制器 - 提供REST API端点
 */
@Controller
@RequestMapping("/api")
public class ApiController {
    
    private final MockEmployeeSnapshotService employeeSnapshotService;
    
    public ApiController(MockEmployeeSnapshotService employeeSnapshotService) {
        this.employeeSnapshotService = employeeSnapshotService;
    }
    
    @GetMapping("/current-count")
    @ResponseBody
    public Map<String, Object> getCurrentCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", employeeSnapshotService.countCurrentActive());
        response.put("timestamp", java.time.LocalDateTime.now());
        return response;
    }
    
    @GetMapping("/metrics")
    @ResponseBody
    public Map<String, Object> getMetrics(@RequestParam(required = false) String date) {
        LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return employeeSnapshotService.getCountsAndRates(selectedDate);
    }
    
    @GetMapping("/snapshot")
    @ResponseBody
    public List<EmployeeIncremental> getSnapshot(@RequestParam(required = false) String date) {
        LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return employeeSnapshotService.getSnapshotByDate(selectedDate);
    }
    
    @GetMapping("/employees/snapshot")
    @ResponseBody
    public List<EmployeeIncremental> getEmployeesSnapshot(@RequestParam(required = false) String date) {
        LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return employeeSnapshotService.getSnapshotByDate(selectedDate);
    }
}