package com.example.testweb;

import com.example.employeesnapshot.entity.EmployeeActive;
import com.example.employeesnapshot.entity.EmployeeIncremental;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestWebController.class)
class TestWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MockEmployeeSnapshotService employeeSnapshotService;

    @BeforeEach
    void setUp() {
        // 设置默认的模拟数据
        when(employeeSnapshotService.countCurrentActive()).thenReturn(95);
        when(employeeSnapshotService.getAllActiveEmployees()).thenReturn(createMockActiveEmployees());
        when(employeeSnapshotService.getSnapshotByDate(any())).thenReturn(createMockSnapshot());
        when(employeeSnapshotService.getCountsAndRates(any())).thenReturn(createMockMetrics());
    }

    @Test
    void testDashboardPage() throws Exception {
        mockMvc.perform(get("/web/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("metrics"))
                .andExpect(model().attributeExists("currentDate"));
    }

    @Test
    void testDashboardPageWithDate() throws Exception {
        String testDate = "2025-01-15";
        mockMvc.perform(get("/web/dashboard").param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("metrics"))
                .andExpect(model().attributeExists("currentDate"));
    }

    @Test
    void testSnapshotPage() throws Exception {
        mockMvc.perform(get("/web/snapshot"))
                .andExpect(status().isOk())
                .andExpect(view().name("snapshot"))
                .andExpect(model().attributeExists("snapshot"))
                .andExpect(model().attributeExists("currentDate"));
    }

    @Test
    void testSnapshotPageWithDate() throws Exception {
        String testDate = "2025-01-15";
        mockMvc.perform(get("/web/snapshot").param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(view().name("snapshot"))
                .andExpect(model().attributeExists("snapshot"))
                .andExpect(model().attributeExists("currentDate"));

        verify(employeeSnapshotService).getSnapshotByDate(LocalDate.parse(testDate));
    }

    @Test
    void testTrendsPage() throws Exception {
        mockMvc.perform(get("/web/trends"))
                .andExpect(status().isOk())
                .andExpect(view().name("trends"))
                .andExpect(model().attributeExists("monthlyData"))
                .andExpect(model().attributeExists("currentDate"));
    }

    @Test
    void testApiTestPage() throws Exception {
        mockMvc.perform(get("/web/api-test"))
                .andExpect(status().isOk())
                .andExpect(view().name("api-test"));
    }

    @Test
    void testGetCurrentCount() throws Exception {
        mockMvc.perform(get("/api/current-count"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(95))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(employeeSnapshotService).countCurrentActive();
    }

    @Test
    void testGetMetrics() throws Exception {
        mockMvc.perform(get("/api/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cur_count").value(95))
                .andExpect(jsonPath("$.prev_month_count").value(92))
                .andExpect(jsonPath("$.prev_year_count").value(88))
                .andExpect(jsonPath("$.newHires").value(5))
                .andExpect(jsonPath("$.terminations").value(2))
                .andExpect(jsonPath("$.hireRate").value(0.05))
                .andExpect(jsonPath("$.terminationRate").value(0.02))
                .andExpect(jsonPath("$.totalEmployees").value(100));

        verify(employeeSnapshotService).getCountsAndRates(any());
    }

    @Test
    void testGetMetricsWithDate() throws Exception {
        String testDate = "2025-01-15";
        mockMvc.perform(get("/api/metrics").param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cur_count").value(95));

        verify(employeeSnapshotService).getCountsAndRates(LocalDate.parse(testDate));
    }

    @Test
    void testGetSnapshot() throws Exception {
        mockMvc.perform(get("/api/snapshot"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employeeId").value(1L))
                .andExpect(jsonPath("$[0].name").value("测试员工"))
                .andExpect(jsonPath("$[0].deptName").value("技术部"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(employeeSnapshotService).getSnapshotByDate(any());
    }

    @Test
    void testGetSnapshotWithDate() throws Exception {
        String testDate = "2025-01-15";
        mockMvc.perform(get("/api/snapshot").param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(employeeSnapshotService).getSnapshotByDate(LocalDate.parse(testDate));
    }

    @Test
    void testGetActiveEmployees() throws Exception {
        mockMvc.perform(get("/api/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employeeId").value(1L))
                .andExpect(jsonPath("$[0].name").value("测试员工"))
                .andExpect(jsonPath("$[0].isActive").value(true));

        verify(employeeSnapshotService).getAllActiveEmployees();
    }

    @Test
    void testTrendsDataApi() throws Exception {
        mockMvc.perform(get("/api/trends"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.labels").isArray())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.labels[0]").exists())
                .andExpect(jsonPath("$.data[0]").isNumber());
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.service").value("employee-snapshot-test"));
    }

    @Test
    void testDashboardWithServiceException() throws Exception {
        when(employeeSnapshotService.getCountsAndRates(any()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/web/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("metrics"));
    }

    @Test
    void testSnapshotWithServiceException() throws Exception {
        when(employeeSnapshotService.getSnapshotByDate(any()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/web/snapshot"))
                .andExpect(status().isOk())
                .andExpect(view().name("snapshot"))
                .andExpect(model().attributeExists("snapshot"));
    }

    @Test
    void testApiWithServiceException() throws Exception {
        when(employeeSnapshotService.countCurrentActive())
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/current-count"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/web/snapshot").param("date", "invalid-date"))
                .andExpect(status().isOk())
                .andExpect(view().name("snapshot"));
    }

    @Test
    void testEmptySnapshotData() throws Exception {
        when(employeeSnapshotService.getSnapshotByDate(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/snapshot"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testEmptyActiveEmployees() throws Exception {
        when(employeeSnapshotService.getAllActiveEmployees())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/active"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testMetricsWithNullValues() throws Exception {
        Map<String, Object> metricsWithNulls = new HashMap<>();
        metricsWithNulls.put("cur_count", null);
        metricsWithNulls.put("prev_month_count", 50);
        
        when(employeeSnapshotService.getCountsAndRates(any()))
                .thenReturn(metricsWithNulls);

        mockMvc.perform(get("/api/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cur_count").doesNotExist())
                .andExpect(jsonPath("$.prev_month_count").value(50));
    }

    @Test
    void testDateRangeValidation() throws Exception {
        // 测试过去日期
        mockMvc.perform(get("/web/snapshot").param("date", "2020-01-01"))
                .andExpect(status().isOk());

        // 测试未来日期
        mockMvc.perform(get("/web/snapshot").param("date", "2030-01-01"))
                .andExpect(status().isOk());

        // 测试边界日期
        mockMvc.perform(get("/web/snapshot").param("date", "1970-01-01"))
                .andExpect(status().isOk());
    }

    @Test
    void testContentTypeNegotiation() throws Exception {
        // 测试接受JSON
        mockMvc.perform(get("/api/current-count")
                .header("Accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // 测试接受任何类型
        mockMvc.perform(get("/api/current-count")
                .header("Accept", "*/*"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testModelAttributes() throws Exception {
        mockMvc.perform(get("/web/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("metrics"))
                .andExpect(model().attributeExists("currentDate"))
                .andExpect(model().attribute("currentDate", LocalDate.now().toString()));
    }

    // 辅助方法
    private List<EmployeeActive> createMockActiveEmployees() {
        List<EmployeeActive> employees = new ArrayList<>();
        EmployeeActive employee = new EmployeeActive();
        employee.setEmployeeId(1L);
        employee.setName("测试员工");
        employee.setDeptName("技术部");
        employee.setIsActive(true);
        employees.add(employee);
        return employees;
    }

    private List<EmployeeIncremental> createMockSnapshot() {
        List<EmployeeIncremental> snapshot = new ArrayList<>();
        EmployeeIncremental employee = new EmployeeIncremental();
        employee.setEmployeeId(1L);
        employee.setName("测试员工");
        employee.setDeptName("技术部");
        employee.setChangeDate(LocalDate.now());
        employee.setIsActive(true);
        snapshot.add(employee);
        return snapshot;
    }

    private Map<String, Object> createMockMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("cur_count", 95);
        metrics.put("prev_month_count", 92);
        metrics.put("prev_year_count", 88);
        metrics.put("mom_change_rate", 0.0326);
        metrics.put("yoy_change_rate", 0.080);
        metrics.put("newHires", 5);
        metrics.put("terminations", 2);
        metrics.put("hireRate", 0.05);
        metrics.put("terminationRate", 0.02);
        metrics.put("totalEmployees", 100);
        return metrics;
    }
}