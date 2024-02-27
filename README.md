# 📘 개요
- 선착순(한정된 재고 및 특정시간 오픈) 및 일반 아이템을 구매 서비스를 제공하는 백엔드 서버입니다.
- MSA 아키텍쳐로 구성되어 있으며, Docker 환경에서 테스트할 수 있습니다.
<br/><br/>
# 📕 기술 스택
- Java 17, Spring Boot 3.1.8
- Spring Cloud(Eureka, Gateway, Feign Client), JPA
- MySQL, Kafka, H2, Redis, Redisson
- JUnit5, Mockito
- Docker, Docker Compose
<br/><br/>
# 📙 Module 구조
#### `common-kafka` : 카프카 공통 기능 추상화 및 설정 모듈
#### `common-redis` : 레디스 공통 기능 추상화 및 설정 모듈
#### `common-service-data` : 카프카 공통 기능 추상화 및 설정 모듈
#### `eureka-service` : 동적으로 각 서비스를 등록하고, 식별하기 위한 서비스
#### `gateway-service` : API Gateway 역할(라우팅, 로드밸런싱, 필터링 등)
#### `item-service-api` : 아이템 도메인 관련 서버 (아이템 정보 제공 등)
#### `order-service-api` : 주문 도메인 관련 서버 (주문 진입, 주문 완료 등)
#### `stock-service-api` : 재고 도메인 관련 서버 (아이템 재고 관리 및 조회 등)
<br/>

# 📘 E-R 다이어그램
# 📕 Architecture & Description
# 📙 요구사항 명세서
# 📕 URL
# 📘 How to run?
```
1. gradle build --exclude-task test
2. docker-compose up -d
```