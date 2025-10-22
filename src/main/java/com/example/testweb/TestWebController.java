package com.example.testweb;

import com.example.employeesnapshot.entity.EmployeeIncremental;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用的Web页面控制器
 * 使用MockEmployeeSnapshotService，不依赖数据库
 */
@Controller
@RequestMapping("/web")
public class TestWebController {

    private final MockEmployeeSnapshotService employeeSnapshotService;

    public TestWebController(MockEmployeeSnapshotService employeeSnapshotService) {
        this.employeeSnapshotService = employeeSnapshotService;
    }

    /**
     * 主页面 - 显示员工总数统计和图表
     */
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        // 默认显示今天的数据
        LocalDate today = LocalDate.now();
        
        try {
            // 获取今天的统计数据
            Map<String, Object> todayMetrics = employeeSnapshotService.getCountsAndRates(today);
            model.addAttribute("todayMetrics", todayMetrics);
            model.addAttribute("selectedDate", today);
            
            // 获取当前在职员工数
            int currentActive = employeeSnapshotService.countCurrentActive();
            model.addAttribute("currentActive", currentActive);
            
        } catch (Exception e) {
            model.addAttribute("error", "无法获取统计数据: " + e.getMessage());
            model.addAttribute("selectedDate", today);
        }
        
        return "dashboard";
    }

    /**
     * 快照页面 - 显示指定日期的员工快照
     */
    @GetMapping("/snapshot")
    public String snapshot(
            @RequestParam(value = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        try {
            // 获取指定日期的员工快照
            List<EmployeeIncremental> snapshot = employeeSnapshotService.getSnapshotByDate(date);
            model.addAttribute("snapshot", snapshot);
            model.addAttribute("selectedDate", date);
            model.addAttribute("employeeCount", snapshot.size());
            
            // 按部门统计
            Map<String, Long> deptStats = snapshot.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    emp -> emp.getDeptName() != null ? emp.getDeptName() : "未分配",
                    java.util.stream.Collectors.counting()
                ));
            model.addAttribute("deptStats", deptStats);
            
        } catch (Exception e) {
            model.addAttribute("error", "无法获取快照数据: " + e.getMessage());
            model.addAttribute("selectedDate", date);
        }
        
        return "snapshot";
    }

    /**
     * 趋势页面 - 显示历史趋势图表
     */
    @GetMapping("/trends")
    public String trends(Model model) {
        try {
            // 获取最近30天的数据用于趋势分析
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            
        } catch (Exception e) {
            model.addAttribute("error", "无法获取趋势数据: " + e.getMessage());
        }
        
        return "trends";
    }

    /**
     * API页面 - 提供交互式API测试
     */
    @GetMapping("/api-test")
    public String apiTest(Model model) {
        return "api-test";
    }
}