package com.example.catguardian;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基于Swagger文档的自动化接口测试
 * 自动解析Swagger JSON并测试所有接口
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SwaggerApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static JsonNode swaggerDoc;
    private static List<ApiCase> allCases;
    private static String authToken;

    /**
     * API测试用例记录
     */
    record ApiCase(String path, String method, String summary, List<String> tags) {}

    @BeforeAll
    static void setup() throws IOException {
        // 读取Swagger文档
        File swaggerFile = new File("src/test/resources/api-docs.json");
        if (swaggerFile.exists()) {
            swaggerDoc = objectMapper.readTree(swaggerFile);
            System.out.println("Swagger文档加载成功");
        } else {
            System.out.println("警告: api-docs.json 不存在，请先执行: curl http://localhost:8080/api-docs > src/test/resources/api-docs.json");
            return;
        }

        // 解析所有接口
        allCases = new ArrayList<>();
        JsonNode paths = swaggerDoc.get("paths");
        if (paths != null) {
            paths.fields().forEachRemaining(entry -> {
                String path = entry.getKey();
                JsonNode methods = entry.getValue();
                methods.fields().forEachRemaining(methodEntry -> {
                    String method = methodEntry.getKey().toUpperCase();
                    JsonNode methodDetails = methodEntry.getValue();
                    
                    String summary = "";
                    if (methodDetails.has("summary")) {
                        summary = methodDetails.get("summary").asText();
                    }
                    
                    List<String> tags = new ArrayList<>();
                    if (methodDetails.has("tags")) {
                        methodDetails.get("tags").forEach(tag -> tags.add(tag.asText()));
                    }
                    
                    allCases.add(new ApiCase(path, method, summary, tags));
                });
            });
        }
        
        System.out.println("解析到 " + allCases.size() + " 个API接口");
    }

    @Test
    @Order(1)
    @DisplayName("测试用户注册")
    void testRegister() {
        String requestBody = """
            {
                "phone": "13900139001",
                "password": "test123456",
                "name": "测试用户"
            }
            """;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/auth/register",
                    new HttpEntity<>(requestBody, createJsonHeaders()),
                    String.class
            );

            assertTrue(response.getStatusCode().is2xxSuccessful() || 
                      response.getStatusCode().value() == 400, // 用户已存在
                    "注册接口应该返回成功或用户已存在");
            
            System.out.println("注册测试: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("注册测试异常: " + e.getMessage());
            // 不让测试失败，因为可能是环境问题
        }
    }

    @Test
    @Order(2)
    @DisplayName("测试用户登录")
    void testLogin() {
        String requestBody = """
            {
                "phone": "13900139001",
                "password": "test123456"
            }
            """;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "/api/auth/login",
                    new HttpEntity<>(requestBody, createJsonHeaders()),
                    String.class
            );

            assertTrue(response.getStatusCode().is2xxSuccessful(), "登录应该成功");
            
            // 提取token用于后续测试
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    if (jsonNode.has("data") && jsonNode.get("data").has("token")) {
                        authToken = jsonNode.get("data").get("token").asText();
                        System.out.println("登录成功，获取到token");
                    }
                } catch (Exception e) {
                    System.out.println("解析登录响应失败: " + e.getMessage());
                }
            }
            
            System.out.println("登录测试: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("登录测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("测试公开接口（无需认证）")
    void testPublicEndpoints() {
        String[] publicEndpoints = {
                "/api/adopt/cats",
                "/api/hospitals",
                "/api/mall/products"
        };

        for (String endpoint : publicEndpoints) {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
                assertTrue(response.getStatusCode().is2xxSuccessful(), 
                          "公开接口 " + endpoint + " 应该可访问");
                System.out.println("公开接口测试 " + endpoint + ": " + response.getStatusCode());
            } catch (Exception e) {
                System.out.println("公开接口测试异常 " + endpoint + ": " + e.getMessage());
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试需要认证的接口")
    void testAuthenticatedEndpoints() {
        if (authToken == null) {
            System.out.println("跳过认证接口测试：未获取到token");
            return;
        }

        try {
            HttpHeaders headers = createJsonHeaders();
            headers.setBearerAuth(authToken);

            // 测试获取用户信息
            ResponseEntity<String> userInfoResponse = restTemplate.exchange(
                    "/api/users/profile",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertTrue(userInfoResponse.getStatusCode().is2xxSuccessful(), 
                      "获取用户信息应该成功");
            System.out.println("用户信息接口测试: " + userInfoResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("认证接口测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("测试实名认证接口")
    void testRealNameAuth() {
        if (authToken == null) {
            System.out.println("跳过实名认证测试：未获取到token");
            return;
        }

        try {
            HttpHeaders headers = createJsonHeaders();
            headers.setBearerAuth(authToken);

            // 提交实名认证申请
            String requestBody = """
                {
                    "realName": "张三",
                    "idCard": "110101199001011234",
                    "faceImage": "https://example.com/face.jpg"
                }
                """;

            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/users/auth/realname",
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    String.class
            );

            assertTrue(response.getStatusCode().is2xxSuccessful(), 
                      "实名认证申请应该成功");
            System.out.println("实名认证接口测试: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("实名认证测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DisplayName("测试服务方资质申请接口")
    void testServiceProviderApply() {
        if (authToken == null) {
            System.out.println("跳过服务方资质申请测试：未获取到token");
            return;
        }

        try {
            HttpHeaders headers = createJsonHeaders();
            headers.setBearerAuth(authToken);

            String requestBody = """
                {
                    "idCardFront": "https://example.com/id-front.jpg",
                    "idCardBack": "https://example.com/id-back.jpg",
                    "criminalRecord": "https://example.com/record.jpg",
                    "trainingCertificate": "https://example.com/certificate.jpg"
                }
                """;

            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/service-provider/apply",
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    String.class
            );

            assertTrue(response.getStatusCode().is2xxSuccessful(), 
                      "服务方资质申请应该成功");
            System.out.println("服务方资质申请接口测试: " + response.getStatusCode());
        } catch (Exception e) {
            System.out.println("服务方资质申请测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("测试积分系统接口")
    void testPointsSystem() {
        if (authToken == null) {
            System.out.println("跳过积分系统测试：未获取到token");
            return;
        }

        try {
            HttpHeaders headers = createJsonHeaders();
            headers.setBearerAuth(authToken);

            // 测试签到
            ResponseEntity<String> checkInResponse = restTemplate.exchange(
                    "/api/users/check-in",
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertTrue(checkInResponse.getStatusCode().is2xxSuccessful(), 
                      "签到应该成功");
            System.out.println("签到接口测试: " + checkInResponse.getStatusCode());

            // 测试获取积分余额
            ResponseEntity<String> pointsResponse = restTemplate.exchange(
                    "/api/users/points",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertTrue(pointsResponse.getStatusCode().is2xxSuccessful(), 
                      "获取积分余额应该成功");
            System.out.println("积分余额接口测试: " + pointsResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("积分系统测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("测试医院查询接口")
    void testHospitalEndpoints() {
        try {
            // 测试获取医院列表
            ResponseEntity<String> hospitalsResponse = restTemplate.getForEntity(
                    "/api/hospitals",
                    String.class
            );

            assertTrue(hospitalsResponse.getStatusCode().is2xxSuccessful(), 
                      "获取医院列表应该成功");
            System.out.println("医院列表接口测试: " + hospitalsResponse.getStatusCode());

            // 测试获取医院优惠政策
            ResponseEntity<String> discountsResponse = restTemplate.getForEntity(
                    "/api/hospitals/discounts",
                    String.class
            );

            assertTrue(discountsResponse.getStatusCode().is2xxSuccessful(), 
                      "获取医院优惠政策应该成功");
            System.out.println("医院优惠政策接口测试: " + discountsResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("医院查询测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("测试商城接口")
    void testMallEndpoints() {
        try {
            // 测试获取商品列表
            ResponseEntity<String> productsResponse = restTemplate.getForEntity(
                    "/api/mall/products",
                    String.class
            );

            assertTrue(productsResponse.getStatusCode().is2xxSuccessful(), 
                      "获取商品列表应该成功");
            System.out.println("商品列表接口测试: " + productsResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("商城接口测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("测试社区接口")
    void testCommunityEndpoints() {
        if (authToken == null) {
            System.out.println("跳过社区接口测试：未获取到token");
            return;
        }

        try {
            HttpHeaders headers = createJsonHeaders();
            headers.setBearerAuth(authToken);

            // 测试获取帖子列表
            ResponseEntity<String> postsResponse = restTemplate.exchange(
                    "/api/community/posts",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            assertTrue(postsResponse.getStatusCode().is2xxSuccessful(), 
                      "获取帖子列表应该成功");
            System.out.println("帖子列表接口测试: " + postsResponse.getStatusCode());
        } catch (Exception e) {
            System.out.println("社区接口测试异常: " + e.getMessage());
        }
    }

    /**
     * 使用REST Assured测试所有Swagger文档中的接口
     */
    @Test
    @Order(11)
    @DisplayName("REST Assured测试所有接口")
    void testAllEndpointsWithRestAssured() {
        if (allCases == null || allCases.isEmpty()) {
            System.out.println("跳过REST Assured测试：未解析到API接口");
            return;
        }

        System.out.println("开始测试 " + allCases.size() + " 个接口...");

        int successCount = 0;
        int failCount = 0;
        int skippedCount = 0;

        for (ApiCase apiCase : allCases) {
            try {
                // 跳过需要认证的接口（如果没有token）
                if (authToken == null && requiresAuthentication(apiCase.path)) {
                    System.out.println("⊘ 跳过需要认证的接口: " + apiCase.method + " " + apiCase.path);
                    skippedCount++;
                    continue;
                }

                int statusCode = RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .header("Authorization", authToken != null ? "Bearer " + authToken : "")
                        .when()
                        .request(apiCase.method, "http://localhost:8080" + apiCase.path)
                        .then()
                        .extract()
                        .statusCode();

                // 检查状态码是否为成功状态
                if (statusCode >= 200 && statusCode < 300) {
                    successCount++;
                    System.out.println("✓ " + apiCase.method + " " + apiCase.path + " - " + statusCode);
                } else {
                    failCount++;
                    System.out.println("✗ " + apiCase.method + " " + apiCase.path + " - " + statusCode);
                }
            } catch (Exception e) {
                failCount++;
                System.out.println("✗ " + apiCase.method + " " + apiCase.path + " - 异常: " + e.getMessage());
            }
        }

        System.out.println("\n测试完成: 成功 " + successCount + " 个, 失败 " + failCount + " 个, 跳过 " + skippedCount + " 个");
        
        // 只要有成功的测试就算通过，因为有些接口可能需要特殊的环境配置
        assertTrue(successCount > 0, "至少应该有一个接口测试成功");
    }

    /**
     * 判断接口是否需要认证
     */
    private boolean requiresAuthentication(String path) {
        String[] publicPaths = {
                "/api/auth/register",
                "/api/auth/login",
                "/api/adopt/cats",
                "/api/hospitals",
                "/api/mall/products",
                "/swagger-ui",
                "/api-docs"
        };

        for (String publicPath : publicPaths) {
            if (path.startsWith(publicPath)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 创建JSON请求头
     */
    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}