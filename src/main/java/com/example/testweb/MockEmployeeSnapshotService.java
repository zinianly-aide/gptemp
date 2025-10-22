package com.example.testweb;

import com.example.employeesnapshot.entity.EmployeeActive;
import com.example.employeesnapshot.entity.EmployeeIncremental;
import com.example.employeesnapshot.entity.UpstreamEmployee;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 模拟的EmployeeSnapshotService，用于测试Web控制器
 * 独立实现，不依赖数据库
 */
@Service("employeeSnapshotService")
public class MockEmployeeSnapshotService {

    public List<EmployeeIncremental> getSnapshotByDate(LocalDate date) {
        // 返回模拟数据
        List<EmployeeIncremental> mockData = new ArrayList<>();
        EmployeeIncremental emp = new EmployeeIncremental();
        emp.setEmployeeId(1L);
        emp.setName("测试员工");
        emp.setDeptName("技术部");
        emp.setChangeDate(LocalDate.now().minusMonths(6));
        emp.setChangeTime(LocalDateTime.now());
        emp.setIsActive(true);
        mockData.add(emp);
        return mockData;
    }

    public Map<String, Object> getCountsAndRates(LocalDate date) {
        Map<String, Object> mockMetrics = new HashMap<>();
        // Add all fields that the template expects
        mockMetrics.put("cur_count", 95);
        mockMetrics.put("prev_month_count", 92);
        mockMetrics.put("prev_year_count", 88);
        mockMetrics.put("mom_change_rate", 0.0326); // 3.26% month-over-month change
        mockMetrics.put("yoy_change_rate", 0.080); // 8.0% year-over-year change
        // Additional fields for completeness
        mockMetrics.put("totalEmployees", 100);
        mockMetrics.put("newHires", 5);
        mockMetrics.put("terminations", 2);
        mockMetrics.put("hireRate", 0.05);
        mockMetrics.put("terminationRate", 0.02);
        return mockMetrics;
    }

    public int countCurrentActive() {
        return 95;
    }

    public List<EmployeeActive> getAllActiveEmployees() {
        return new ArrayList<>();
    }

    public void ingestDaily(List<UpstreamEmployee> upstreamList, LocalDate today) {
        // Mock implementation - do nothing
    }

    public void initializeFullData(List<UpstreamEmployee> upstreamList, LocalDate initDate) {
        // Mock implementation - do nothing
    }
}