package com.example.testweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestWebApplication.class
)
class TestWebApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testApplicationStartsSuccessfully() {
        // 测试应用程序能够正常启动
        assertNotNull(restTemplate, "TestRestTemplate should be autowired");
    }

    @Test
    void testRootPathRedirect() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Location"));
        assertEquals("/web/dashboard", response.getHeaders().getFirst("Location"));
    }

    @Test
    void testDashboardPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/web/dashboard", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("员工快照系统"));
        assertTrue(response.getBody().contains("dashboard"));
    }

    @Test
    void testSnapshotPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/web/snapshot", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("员工快照"));
        assertTrue(response.getBody().contains("snapshot"));
    }

    @Test
    void testTrendsPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/web/trends", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("趋势分析"));
        assertTrue(response.getBody().contains("trends"));
    }

    @Test
    void testApiTestPage() {
        ResponseEntity<String> response = restTemplate.getForEntity("/web/api-test", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("API测试"));
        assertTrue(response.getBody().contains("api-test"));
    }

    @Test
    void testCurrentCountApi() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/current-count", Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("count"));
        assertTrue(response.getBody().containsKey("timestamp"));
        
        Integer count = (Integer) response.getBody().get("count");
        assertNotNull(count);
        assertTrue(count >= 0);
    }

    @Test
    void testMetricsApi() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/metrics", Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证必要的指标字段
        assertTrue(response.getBody().containsKey("cur_count"));
        assertTrue(response.getBody().containsKey("prev_month_count"));
        assertTrue(response.getBody().containsKey("prev_year_count"));
        assertTrue(response.getBody().containsKey("newHires"));
        assertTrue(response.getBody().containsKey("terminations"));
        assertTrue(response.getBody().containsKey("hireRate"));
        assertTrue(response.getBody().containsKey("terminationRate"));
        assertTrue(response.getBody().containsKey("totalEmployees"));
        
        // 验证数值类型
        Object curCount = response.getBody().get("cur_count");
        assertNotNull(curCount);
        assertTrue(curCount instanceof Integer || curCount instanceof Number);
    }

    @Test
    void testSnapshotApi() {
        ResponseEntity<Object[]> response = restTemplate.getForEntity("/api/snapshot", Object[].class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证返回的是数组
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    void testActiveEmployeesApi() {
        ResponseEntity<Object[]> response = restTemplate.getForEntity("/api/active", Object[].class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证返回的是数组
        assertTrue(response.getBody().length >= 0);
    }

    @Test
    void testTrendsApi() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/trends", Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证趋势数据格式
        assertTrue(response.getBody().containsKey("labels"));
        assertTrue(response.getBody().containsKey("data"));
    }

    @Test
    void testHealthApi() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/health", Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // 验证健康检查响应
        assertEquals("UP", response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
        assertEquals("employee-snapshot-test", response.getBody().get("service"));
    }

    @Test
    void testApiWithDateParameters() {
        // 测试带日期参数的API
        ResponseEntity<Map> response = restTemplate.getForEntity(
            "/api/metrics?date=2025-01-15", Map.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("cur_count"));
    }

    @Test
    void testWebPagesWithDateParameters() {
        // 测试带日期参数的Web页面
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/web/snapshot?date=2025-01-15", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("员工快照"));
    }

    @Test
    void testInvalidDateParameter() {
        // 测试无效日期参数
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/web/snapshot?date=invalid-date", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testNonExistentEndpoint() {
        // 测试不存在的端点
        ResponseEntity<String> response = restTemplate.getForEntity("/non-existent", String.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testApiResponseContentType() {
        // 测试API响应的Content-Type
        ResponseEntity<String> response = restTemplate.getForEntity("/api/current-count", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentType().toString().contains("application/json"));
    }

    @Test
    void testWebPageResponseContentType() {
        // 测试Web页面响应的Content-Type
        ResponseEntity<String> response = restTemplate.getForEntity("/web/dashboard", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentType().toString().contains("text/html"));
    }

    @Test
    void testServiceDependencies() {
        // 验证服务依赖是否正确注入
        TestWebApplication application = new TestWebApplication();
        assertNotNull(application);
        
        // 验证Mock服务能够正常工作
        MockEmployeeSnapshotService service = new MockEmployeeSnapshotService();
        assertNotNull(service);
        assertTrue(service.countCurrentActive() >= 0);
        assertNotNull(service.getAllActiveEmployees());
        assertNotNull(service.getSnapshotByDate(java.time.LocalDate.now()));
        assertNotNull(service.getCountsAndRates(java.time.LocalDate.now()));
    }

    @Test
    void testApplicationConfiguration() {
        // 验证应用程序配置
        assertNotNull(restTemplate);
        
        // 测试多个端点以确保应用程序正常运行
        testRootPathRedirect();
        testDashboardPage();
        testCurrentCountApi();
        testHealthApi();
    }
}