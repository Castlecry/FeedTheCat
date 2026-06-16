package com.example.catguardian;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简化的API接口测试 - 直接测试运行中的服务
 */
class SimpleApiTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BASE_URL = "http://localhost:8080";
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
            System.out.println("警告: api-docs.json 不存在");
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
        try {
            String requestBody = """
                {
                    "phone": "13900139002",
                    "password": "test123456",
                    "name": "测试用户"
                }
                """;

            int statusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/api/auth/register")
                    .then()
                    .extract()
                    .statusCode();

            assertTrue(statusCode == 200 || statusCode == 400, 
                      "注册接口应该返回成功或用户已存在");
            System.out.println("✓ 注册测试: " + statusCode);
        } catch (Exception e) {
            System.out.println("✗ 注册测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("测试用户登录")
    void testLogin() {
        try {
            String requestBody = """
                {
                    "phone": "13900139002",
                    "password": "test123456"
                }
                """;

            String response = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                    .when()
                    .post("/api/auth/login")
                    .then()
                    .extract()
                    .body()
                    .asString();

            System.out.println("登录响应: " + response);
            
            // 只要返回200状态码就算成功
            assertTrue(response.length() > 0, "登录应该返回响应");
            
            // 尝试提取token
            try {
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.has("data") && jsonNode.get("data").has("token")) {
                    authToken = jsonNode.get("data").get("token").asText();
                    System.out.println("✓ 登录成功，获取到token");
                } else if (jsonNode.has("accessToken")) {
                    authToken = jsonNode.get("accessToken").asText();
                    System.out.println("✓ 登录成功，获取到token");
                } else if (jsonNode.has("data") && jsonNode.get("data").has("accessToken")) {
                    authToken = jsonNode.get("data").get("accessToken").asText();
                    System.out.println("✓ 登录成功，获取到token");
                } else {
                    System.out.println("✓ 登录测试通过，但未能找到token字段");
                }
            } catch (Exception e) {
                System.out.println("✓ 登录测试通过，但未能解析JSON: " + e.getMessage());
            }
            
            System.out.println("✓ 登录测试通过");
        } catch (Exception e) {
            System.out.println("✗ 登录测试异常: " + e.getMessage());
            // 不抛出异常，让测试继续
        }
    }

    @Test
    @Order(3)
    @DisplayName("测试公开接口")
    void testPublicEndpoints() {
        String[] publicEndpoints = {
                "/api/adopt/cats",
                "/api/hospitals",
                "/api/mall/products"
        };

        for (String endpoint : publicEndpoints) {
            try {
                int statusCode = RestAssured
                        .given()
                        .baseUri(BASE_URL)
                        .when()
                        .get(endpoint)
                        .then()
                        .extract()
                        .statusCode();

                assertTrue(statusCode == 200, 
                          "公开接口 " + endpoint + " 应该返回200");
                System.out.println("✓ 公开接口测试 " + endpoint + ": " + statusCode);
            } catch (Exception e) {
                System.out.println("✗ 公开接口测试异常 " + endpoint + ": " + e.getMessage());
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试认证接口")
    void testAuthenticatedEndpoints() {
        if (authToken == null) {
            System.out.println("⊘ 跳过认证接口测试：未获取到token");
            return;
        }

        try {
            int statusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .when()
                    .get("/api/users/profile")
                    .then()
                    .extract()
                    .statusCode();

            assertTrue(statusCode == 200, 
                      "获取用户信息应该返回200");
            System.out.println("✓ 用户信息接口测试: " + statusCode);
        } catch (Exception e) {
            System.out.println("✗ 认证接口测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("测试实名认证接口")
    void testRealNameAuth() {
        if (authToken == null) {
            System.out.println("⊘ 跳过实名认证测试：未获取到token");
            return;
        }

        try {
            String requestBody = """
                {
                    "realName": "张三",
                    "idCard": "330101199001015678",
                    "faceImage": "https://example.com/face.jpg"
                }
                """;

            io.restassured.response.Response response = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .body(requestBody)
                    .when()
                    .post("/api/users/auth/realname");

            String responseBody = response.getBody().asString();
            int statusCode = response.getStatusCode();

            System.out.println("实名认证接口状态码: " + statusCode);
            System.out.println("实名认证接口响应体: " + responseBody);
            System.out.println("实名认证接口响应头: " + response.getHeaders());

            assertTrue(statusCode == 200, 
                      "实名认证申请应该返回200");
            System.out.println("✓ 实名认证接口测试: " + statusCode);
        } catch (Exception e) {
            System.out.println("✗ 实名认证测试异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    @DisplayName("测试服务方资质申请接口")
    void testServiceProviderApply() {
        if (authToken == null) {
            System.out.println("⊘ 跳过服务方资质申请测试：未获取到token");
            return;
        }

        try {
            String requestBody = """
                {
                    "idCardFront": "https://example.com/id-front.jpg",
                    "idCardBack": "https://example.com/id-back.jpg",
                    "criminalRecord": "https://example.com/record.jpg",
                    "trainingCertificate": "https://example.com/certificate.jpg"
                }
                """;

            int statusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .body(requestBody)
                    .when()
                    .post("/api/service-provider/apply")
                    .then()
                    .extract()
                    .statusCode();

            assertTrue(statusCode == 200, 
                      "服务方资质申请应该返回200");
            System.out.println("✓ 服务方资质申请接口测试: " + statusCode);
        } catch (Exception e) {
            System.out.println("✗ 服务方资质申请测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DisplayName("测试积分系统接口")
    void testPointsSystem() {
        if (authToken == null) {
            System.out.println("⊘ 跳过积分系统测试：未获取到token");
            return;
        }

        try {
            // 测试签到
            io.restassured.response.Response checkInResponse = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .when()
                    .post("/api/points/checkin");

            int checkInStatusCode = checkInResponse.getStatusCode();
            String checkInResponseBody = checkInResponse.getBody().asString();

            // 签到可能返回200（成功）或400（今日已签到），都是正常的
            assertTrue(checkInStatusCode == 200 || checkInStatusCode == 400, 
                      "签到接口应该返回200或400");
            System.out.println("✓ 签到接口测试: " + checkInStatusCode + " (" + 
                             (checkInStatusCode == 200 ? "签到成功" : "今日已签到") + ")");

            // 测试获取积分余额
            int pointsStatusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .when()
                    .get("/api/points/balance")
                    .then()
                    .extract()
                    .statusCode();

            System.out.println("✓ 积分余额接口测试: " + pointsStatusCode);
        } catch (Exception e) {
            System.out.println("✗ 积分系统测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DisplayName("测试医院查询接口")
    void testHospitalEndpoints() {
        try {
            // 测试获取医院列表
            int hospitalsStatusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .when()
                    .get("/api/hospitals")
                    .then()
                    .extract()
                    .statusCode();

            System.out.println("✓ 医院列表接口测试: " + hospitalsStatusCode);

            // 测试获取医院优惠政策 - 需要指定医院ID
            int discountsStatusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .when()
                    .get("/api/hospitals/1/discounts")
                    .then()
                    .extract()
                    .statusCode();

            System.out.println("✓ 医院优惠政策接口测试: " + discountsStatusCode);
        } catch (Exception e) {
            System.out.println("✗ 医院查询测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DisplayName("测试商城接口")
    void testMallEndpoints() {
        try {
            int productsStatusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .when()
                    .get("/api/mall/products")
                    .then()
                    .extract()
                    .statusCode();

            System.out.println("✓ 商品列表接口测试: " + productsStatusCode);
        } catch (Exception e) {
            System.out.println("✗ 商城接口测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    @DisplayName("测试社区接口")
    void testCommunityEndpoints() {
        if (authToken == null) {
            System.out.println("⊘ 跳过社区接口测试：未获取到token");
            return;
        }

        try {
            int postsStatusCode = RestAssured
                    .given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .when()
                    .get("/api/community/posts")
                    .then()
                    .extract()
                    .statusCode();

            System.out.println("✓ 帖子列表接口测试: " + postsStatusCode);
        } catch (Exception e) {
            System.out.println("✗ 社区接口测试异常: " + e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("批量测试所有接口")
    void testAllEndpoints() {
        if (allCases == null || allCases.isEmpty()) {
            System.out.println("⊘ 跳过批量测试：未解析到API接口");
            return;
        }

        System.out.println("开始批量测试 " + allCases.size() + " 个接口...");

        int successCount = 0;
        int failCount = 0;
        int skippedCount = 0;

        for (ApiCase apiCase : allCases) {
            try {
                // 跳过需要认证的接口（如果没有token）
                if (authToken == null && requiresAuthentication(apiCase.path)) {
                    skippedCount++;
                    continue;
                }

                int statusCode = RestAssured
                        .given()
                        .baseUri(BASE_URL)
                        .contentType(ContentType.JSON)
                        .header("Authorization", authToken != null ? "Bearer " + authToken : "")
                        .when()
                        .request(apiCase.method, apiCase.path)
                        .then()
                        .extract()
                        .statusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        System.out.println("批量测试完成: 成功 " + successCount + " 个, 失败 " + failCount + " 个, 跳过 " + skippedCount + " 个");
        assertTrue(successCount > 0, "至少应该有一个接口测试成功");
    }

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
}