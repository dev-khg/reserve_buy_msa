# 📘 개요
- 선착순(한정된 재고 및 특정시간 오픈) 및 일반 아이템을 구매 서비스를 제공하는 백엔드 서버입니다.
- MSA 아키텍쳐로 구성되어 있으며, Docker 환경에서 테스트할 수 있습니다.
<br/><br/>
# 📕 기술 스택
- Java 17, Spring Boot 3.1.8
- Spring Cloud(Eureka, Gateway, Feign Client), JPA
- MySQL, Kafka, H2, Redis, Redisson
- JUnit5, Mockito, JMeter
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
<p align='center'>
    <img src='./image/erd.png' height='500'>
</p>

# 📕 Architecture
<p align='center'>
    <img src='./image/architecture.png'>
</p>

# 📙 Trouble Shooting

<a href="https://velog.io/@knhng/MSA-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%84%A0%EC%B0%A9%EC%88%9C-%EA%B5%AC%EB%A7%A4-%EC%84%9C%EB%B2%84-Redis-%ED%99%9C%EC%9A%A9">#1 레디스를 활용하여 성능 향상 시키기 및 발생한 문제 대처 (1.캐싱, 2.분산 락, 3. 루아 스크립트 적용)</a>
<br/><br/>
<a href="https://velog.io/@knhng/MSA-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%84%A0%EC%B0%A9%EC%88%9C-%EA%B5%AC%EB%A7%A4-%EC%84%9C%EB%B2%84-2.-%EC%B9%B4%ED%94%84%EC%B9%B4-%ED%99%9C%EC%9A%A9">#2 카프카를 활용하여 TPS 높이기</a>

<a href="https://velog.io/@knhng/MSA-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%84%A0%EC%B0%A9%EC%88%9C-%EA%B5%AC%EB%A7%A4-%EC%84%9C%EB%B2%84-3.-%EA%B0%80%EC%83%81-%EC%8A%A4%EB%A0%88%EB%93%9C%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B3%A0%EC%B0%B0-s8yf859w">#3 가상 스레드에 대한 고찰</a>
<br/>

# 📘 API
- API 요청 시, Prefix로 /api 요청해야 합니다.
- Http 프로토콜, 8000 포트로 요청하면 테스트해볼 수 있습니다.
- 로그인이 필요한 요청은 Authorization 헤더에 Long 범위의 정수를 삽입하여 요청해야 합니다. (로그인은 중점 사항이 아님.)

    <details>
    <summary>아이템 도메인 API</summary>

    - **아이템 단건 조회**
        **Path** : /item/{itemNumber}    
        **Response Example**
        ``` JSON
        {
            "success" : true,
            "data" : {
                "itemNumber" : 1,
                "name" : "라면",
                "content" : "라면입니다.",
                "price" : 1000,
                "type" : "{TIME_DEAL | GENERAL}", // {선착순 | 일반}
                "start_at" : "YYYY-MM-DD hh:mm:ss" //[optional] type = 선착순인 경우
            }
        }
        ```
    - 아이템 다중 조회 <br/>
        **Path** : /item<br/> 
        **Response Example**
        ```JSON
        {
            "success" : true,
            "data" : [
                {
                    "itemNumber" : 1,
                    "name" : "라면",
                    "price" : 1000,
                    "type" : "{TIME_DEAL | GENERAL}",
                    "startAt" : "YYYY-MM-DD hh:mm:ss" //[optional] type = TIME_DEAL인 경우
                },
                // ...
            ]
        }
        ```
    - 아이템 <-> 주문 서비스 캐시용 (외부 노출x)<br/>
        **Path** : /item/{itemNumber}/cache<br/>
        **Response Example**
        ```JSON
        {
            "itemNumber" : 1,
            "price" : 1000,
            "type" : "{TIME_DEAL | GENERAL}",
            "startAt" : "YYYY-MM-DD hh:mm:ss" //[optional] type = TIME_DEAL인 경우
        }
        ```

    </details>

    <details>
    <summary>재고 도메인 API</summary>

    - 재고 조회<br/>
        **Path** : /stock/{itemNumber}<br/>
        **Response Example**
        ```JSON
        {
            "success" : true,
            "data" : 100 // 아이템 재고
        }
        ```
    - 재고 <-> 주문 서비스 캐시용 (외부 노출x)<br/>
        **Path** : /stock/{itemNumber}/cache <br/>
        **Response Example**
        ```JSON
        {
            "success" : true,
            "data" : 100 // 아이템 재고
        }
        ```
    </details>

    <details>
    <summary>주문 도메인 API</summary>

    - **주문 진입 [로그인 필요]**<br/>
        **Path** : /order<br/>
        **Request Example** <br/>
        ```JSON
        {
            "itemNumber" : 1,
            "count" : 1
        }
        ```
        **Response Example**
        ```JSON
        {
            "success" : true,
            "data" : "orderId"
        }
        ```
    - **주문 결제 처리 [로그인 필요]**<br/>
        **Path** : /order/{orderId} <br/>        
        **Response Example**
        ```JSON
        {
            "success" : true,
            "data" : 100 // 아이템 재고
        }
        ```
    </details>

# 📕 How to run?
```
1. gradle build --exclude-task test
2. docker-compose up -d
```
