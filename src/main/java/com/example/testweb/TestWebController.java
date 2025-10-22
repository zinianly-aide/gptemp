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
        }
        
        return "dashboard";
    }

    /**
     * 查看指定日期的员工快照数据
     */
    @GetMapping("/snapshot")
    public String snapshot(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                          Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        
        try {
            // 获取指定日期的员工快照数据
            List<EmployeeIncremental> snapshotData = employeeSnapshotService.getSnapshotByDate(date);
            model.addAttribute("snapshotData", snapshotData);
            model.addAttribute("selectedDate", date);
        } catch (Exception e) {
            model.addAttribute("error", "无法获取快照数据: " + e.getMessage());
        }
        
        return "snapshot";
    }

    /**
     * API控制器 - 提供JSON格式的数据接口
     */
    @Controller
    @RequestMapping("/api")
    static class ApiController {
        
        private final MockEmployeeSnapshotService employeeSnapshotService;
        
        public ApiController(MockEmployeeSnapshotService employeeSnapshotService) {
            this.employeeSnapshotService = employeeSnapshotService;
        }
        
        /**
         * 获取指定日期的员工统计数据
         */
        @GetMapping("/metrics")
        @ResponseBody
        public Map<String, Object> getMetrics(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
            if (date == null) {
                date = LocalDate.now();
            }
            
            try {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", employeeSnapshotService.getCountsAndRates(date));
                return result;
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                return errorResult;
            }
        }
        
        /**
         * 获取指定日期的员工快照数据
         */
        @GetMapping("/snapshot")
        @ResponseBody
        public Map<String, Object> getSnapshot(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
            if (date == null) {
                date = LocalDate.now();
            }
            
            try {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("data", employeeSnapshotService.getSnapshotByDate(date));
                return result;
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                return errorResult;
            }
        }
    }
}