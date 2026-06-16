# 自动化接口测试说明

基于Swagger文档的自动化接口测试框架，无需额外工具，直接在项目内部运行。

## 功能特点

- ✅ 自动解析Swagger JSON文档
- ✅ 批量测试所有API接口
- ✅ 支持认证接口测试
- ✅ 详细的测试报告
- ✅ 集成到CI/CD流水线
- ✅ 零工具安装成本

## 快速开始

### 1. 添加依赖

依赖已在 `pom.xml` 中配置：

```xml
<!-- REST Assured：链式调接口 -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>

<!-- Jackson Databind：解析 Swagger JSON -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <scope>test</scope>
</dependency>
```

### 2. 导出Swagger文档

在服务运行时执行：

```bash
curl http://localhost:8080/api-docs > src/test/resources/api-docs.json
```

### 3. 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定的测试类
mvn test -Dtest=SwaggerApiTest

# 运行特定的测试方法
mvn test -Dtest=SwaggerApiTest#testLogin
```

## 测试覆盖范围

### 核心功能测试

| 测试项 | 说明 |
|--------|------|
| 用户注册 | 测试用户注册接口 |
| 用户登录 | 测试用户登录并获取token |
| 公开接口 | 测试无需认证的公开接口 |
| 认证接口 | 测试需要认证的接口 |
| 实名认证 | 测试实名认证申请接口 |
| 服务方资质 | 测试服务方资质申请接口 |
| 积分系统 | 测试签到和积分查询接口 |
| 医院查询 | 测试医院和优惠政策查询接口 |
| 商城接口 | 测试商品列表查询接口 |
| 社区接口 | 测试社区帖子接口 |
| 全量接口 | 使用REST Assured测试所有Swagger接口 |

## 测试类结构

```
SwaggerApiTest.java
├── @BeforeAll: 解析Swagger文档
├── @Test @Order(1): 用户注册测试
├── @Test @Order(2): 用户登录测试
├── @Test @Order(3): 公开接口测试
├── @Test @Order(4): 认证接口测试
├── @Test @Order(5): 实名认证测试
├── @Test @Order(6): 服务方资质测试
├── @Test @Order(7): 积分系统测试
├── @Test @Order(8): 医院查询测试
├── @Test @Order(9): 商城接口测试
├── @Test @Order(10): 社区接口测试
└── @Test @Order(11): REST Assured全量测试
```

## 配置说明

### 测试环境配置

测试配置文件：`src/test/resources/application-test.yml`

主要配置项：
- 数据库连接
- Redis连接
- RabbitMQ连接
- JWT密钥
- MinIO配置
- 日志级别

### 认证处理

测试会自动：
1. 注册测试用户
2. 登录获取JWT token
3. 在需要认证的接口中使用token

## 自定义测试

### 添加新的测试用例

在 `SwaggerApiTest.java` 中添加新的测试方法：

```java
@Test
@Order(12)
@DisplayName("测试新功能")
void testNewFeature() {
    if (authToken == null) {
        System.out.println("跳过测试：未获取到token");
        return;
    }

    HttpHeaders headers = createJsonHeaders();
    headers.setBearerAuth(authToken);

    // 你的测试逻辑
    ResponseEntity<String> response = restTemplate.exchange(
            "/api/new-endpoint",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
    );

    assertTrue(response.getStatusCode().is2xxSuccessful(), 
              "新功能应该成功");
}
```

### 修改公开接口列表

在 `requiresAuthentication` 方法中添加新的公开接口：

```java
private boolean requiresAuthentication(String path) {
    String[] publicPaths = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/your-new-public-endpoint"  // 添加新的公开接口
    };
    // ...
}
```

## CI/CD集成

### GitHub Actions示例

```yaml
name: API Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Start MySQL
      run: |
        sudo systemctl start mysql
        
    - name: Start Redis
      run: |
        sudo systemctl start redis
        
    - name: Start RabbitMQ
      run: |
        sudo systemctl start rabbitmq-server
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
      
    - name: Start Application
      run: |
        java -jar target/cat-guardian-1.0.0.jar &
        sleep 30
        
    - name: Export Swagger Docs
      run: |
        curl http://localhost:8080/api-docs > src/test/resources/api-docs.json
        
    - name: Run Tests
      run: mvn test
```

## 常见问题

### 1. 测试失败：api-docs.json 不存在

**解决方案**：确保服务正在运行，然后执行：

```bash
curl http://localhost:8080/api-docs > src/test/resources/api-docs.json
```

### 2. 数据库连接失败

**解决方案**：检查 `application-test.yml` 中的数据库配置是否正确。

### 3. Redis连接失败

**解决方案**：确保Redis服务正在运行，或修改测试配置跳过Redis依赖。

### 4. 认证失败

**解决方案**：检查JWT配置和用户认证逻辑，确保测试用户可以正常登录。

## 测试报告

测试运行后会生成详细的控制台输出，包括：

- 每个接口的测试结果
- 成功/失败统计
- 详细的错误信息
- 性能数据

## 最佳实践

1. **定期更新Swagger文档**：接口变更后及时更新 `api-docs.json`
2. **隔离测试数据**：使用独立的测试数据库
3. **清理测试数据**：测试后清理产生的测试数据
4. **并行测试**：对于独立接口可以使用并行测试提高效率
5. **持续集成**：将测试集成到CI/CD流水线中

## 扩展功能

### 添加性能测试

```java
@Test
@DisplayName("性能测试")
void performanceTest() {
    long startTime = System.currentTimeMillis();
    
    // 执行接口调用
    
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    
    assertTrue(duration < 1000, "接口响应时间应小于1秒");
}
```

### 添加负载测试

```java
@Test
@DisplayName("负载测试")
void loadTest() {
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < threadCount; i++) {
        futures.add(executor.submit(() -> {
            // 执行接口调用
        }));
    }
    
    // 等待所有任务完成
    for (Future<?> future : futures) {
        future.get();
    }
    
    executor.shutdown();
}
```

## 维护说明

- 定期更新测试用例以覆盖新功能
- 监控测试执行时间，优化慢速测试
- 及时修复失败的测试用例
- 保持测试代码的清晰和可维护性