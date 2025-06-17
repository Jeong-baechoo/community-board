# Community Board - DDD 기반 게시판 프로젝트

## 프로젝트 개요

Spring Boot와 Domain-Driven Design(DDD)을 기반으로 구현한 커뮤니티 게시판 프로젝트입니다.

### 기술 스택
- **Backend**: Spring Boot 3.5.0, Java 21
- **Database**: H2 (개발), MySQL (운영 예정)
- **Security**: Spring Security (JWT 미사용)
- **Build**: Gradle 8.14.2
- **CI/CD**: GitHub Actions
- **Container**: Docker, Docker Hub
- **Architecture**: Domain-Driven Design (DDD)

## 프로젝트 구조

### Feature-first Architecture (기능 우선 구조)
```
src/main/java/com/example/communityboard/
├── member/                     # 회원 도메인
│   ├── domain/                # 도메인 계층
│   │   ├── entity/           # Member 엔티티
│   │   ├── vo/              # 값 객체 (LoginId, Password, Email, Nickname)
│   │   └── repository/       # 도메인 인터페이스
│   ├── application/          # 응용 계층
│   │   ├── service/         # MemberService
│   │   ├── dto/             # LoginRequest, SignupRequest, MemberResponse
│   │   └── exception/       # 도메인 예외
│   ├── infrastructure/       # 인프라 계층
│   │   └── persistence/     # JPA 구현체
│   └── presentation/         # 표현 계층
│       └── controller/      # MemberController
├── board/                      # 게시판 도메인 (예정)
├── comment/                    # 댓글 도메인 (예정)
└── common/                     # 공통 모듈
    ├── config/                # 설정 (Security, JPA 등)
    ├── dto/                   # 공통 응답 DTO
    └── exception/             # 전역 예외 처리
```

### 구조 변경 이력
- **이전**: Layer-first (계층 우선) - domain/, application/, infrastructure/, presentation/
- **현재**: Feature-first (기능 우선) - member/, board/, comment/
- **이유**: 각 도메인의 응집도를 높이고, 도메인별 독립적인 개발과 관리가 가능

## DDD 설계 고민 사항

### 1. Member 도메인 설계

#### 1.1 엔티티 위치 문제
- **초기 문제**: Member 엔티티가 `domain/board/entity` 폴더에 위치
- **해결**: `domain/member/entity`로 이동하여 독립적인 도메인으로 분리

#### 1.2 정적 팩토리 메서드
```java
// 초기 설계
public static Member create(...) { }

// 개선된 설계
public static Member register(...) { }
```
- **고민**: 단순한 `create`는 생성자와 차이가 없음
- **개선**: `register`로 명명하여 도메인 의도를 명확히 표현

#### 1.3 검증 로직 위치
- **초기**: Member 엔티티 내부에 모든 검증 로직 존재
- **개선**: 각 값 객체로 검증 로직 분산
- **이점**: 응집도 향상, 재사용성 증가

### 2. 값 객체(Value Object) 도입

#### 2.1 구현된 값 객체
- **LoginId**: 4-20자, 영문/숫자만 허용
- **Password**: 최소 8자, 영문/숫자/특수문자 중 2종류 이상
- **Nickname**: 2-10자, 한글/영문/숫자
- **Email**: 표준 이메일 형식 검증

#### 2.2 값 객체 도입의 장단점

**장점:**
- 도메인 규칙이 명확하게 캡슐화됨
- 타입 안정성 보장
- 불변성(Immutability) 보장
- 유효하지 않은 상태 생성 불가능

**단점:**
- 단순한 String wrapper가 될 수 있음
- 과도한 복잡도 증가 가능성
- JPA 매핑 시 추가 설정 필요

#### 2.3 값 객체 사용 기준
```java
// 값 객체가 적절한 경우
@Embeddable
public class Email {
    private String value;
    // 복잡한 검증 로직
    // 이메일 관련 도메인 메서드
}

// 과도한 설계일 수 있는 경우
@Embeddable
public class SimpleString {
    private String value;
    // 단순 검증만 존재
}
```

### 3. JPA와 DDD의 타협점

#### 3.1 기본 생성자 문제
```java
@NoArgsConstructor(AccessLevel.PROTECTED)
```
- **이유**: JPA는 리플렉션을 통해 엔티티 생성
- **타협**: PROTECTED로 외부 접근 차단하면서 JPA 요구사항 충족

#### 3.2 @Embeddable과 값 객체
- JPA의 `@Embeddable`을 활용하여 값 객체 구현
- 데이터베이스 컬럼과 도메인 모델의 매핑

### 4. 설계 원칙과 트레이드오프

#### 4.1 언제 값 객체를 사용할 것인가?
1. **사용하기 좋은 경우**:
   - 복잡한 검증 로직이 있을 때
   - 도메인 특화 메서드가 필요할 때
   - 불변성이 중요할 때

2. **재고려가 필요한 경우**:
   - 단순한 문자열 검증만 있을 때
   - 비즈니스 로직이 거의 없을 때
   - 성능이 매우 중요한 경우

#### 4.2 실용적 접근
```java
// 복잡한 도메인 로직이 있는 경우 - 값 객체 사용
public class Password {
    public boolean matches(String raw) { }
    public String encrypt() { }
}

// 단순한 경우 - 어노테이션 활용
@Pattern(regexp = "^[a-zA-Z0-9]{4,20}$")
private String loginId;
```

### 5. 향후 개선 사항

1. **도메인 이벤트 추가**
   - MemberRegistered
   - PasswordChanged
   - ProfileUpdated

2. **애그리거트 경계 명확화**
   - Member 애그리거트의 범위 정의
   - 다른 도메인과의 관계 설정

3. **도메인 서비스 도입**
   - 중복 검사 로직
   - 암호화 처리

## 6. Repository 계층 분리 설계

### 6.1 구조
```
domain/member/repository/MemberRepository.java      # 도메인 인터페이스
infrastructure/persistence/member/
├── MemberRepositoryImpl.java                      # 인터페이스 구현체
└── MemberJpaRepository.java                       # Spring Data JPA
```

### 6.2 설계 의도
- **의존성 역전 원칙(DIP)**: 도메인이 인프라에 의존하지 않고, 인프라가 도메인에 의존

### 6.3 장점
1. **테스트 용이성**: Mock 객체로 쉽게 대체 가능
2. **기술 독립성**: JPA → MyBatis 등 기술 스택 변경 시 도메인 코드 수정 불필요
3. **명확한 계층 분리**: 각 계층의 책임이 명확함
4. **다중 구현 지원**: 상황별 다른 구현체 사용 가능 (캐싱, 인메모리 등)

### 6.4 트레이드오프
1. **복잡도 증가**: 단순한 CRUD에도 인터페이스/구현체 분리
2. **코드 중복**: 메서드 시그니처가 여러 곳에 반복
3. **개발 속도**: 초기 구현 시 더 많은 코드 작성 필요

### 6.5 실용적 접근
```java
// 복잡한 도메인 로직이 있는 경우 - 분리 권장
public interface OrderRepository {
    Order findByIdWithItems(Long id);
    List<Order> findPendingOrdersWithDelivery();
}

// 단순 CRUD만 있는 경우 - 직접 JpaRepository 사용 고려
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
```

## API 엔드포인트

### 회원 관리 API

#### 1. 회원가입
- **URL**: `POST /api/members/signup`
- **Request Body**:
```json
{
    "loginId": "testuser123",
    "password": "Password123!",
    "nickname": "테스트유저",
    "email": "test@example.com"
}
```
- **Validation Rules**:
  - loginId: 4-20자, 영문/숫자만
  - password: 8-20자, 대소문자/숫자/특수문자 포함
  - nickname: 2-10자
  - email: 유효한 이메일 형식

#### 2. 로그인
- **URL**: `POST /api/members/login`
- **Request Body**:
```json
{
    "loginId": "testuser123",
    "password": "Password123!"
}
```

## 실행 방법

### 1. 로컬 실행
```bash
# 프로젝트 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

### 2. Docker 실행
```bash
# Docker 이미지 빌드
docker build -t community-board .

# 컨테이너 실행
docker run -p 8080:8080 community-board

# Docker Hub에서 최신 이미지 실행
docker pull baechoo/community-board:latest
docker run -p 8080:8080 baechoo/community-board:latest
```

## CI/CD 파이프라인

### GitHub Actions 워크플로우
1. **트리거**: main, develop, feature/** 브랜치 push/PR
2. **빌드 및 테스트**: Java 21, Gradle
3. **테스트 리포트**: JUnit 테스트 결과 자동 생성
4. **Docker 이미지**: 테스트 성공 시 Docker Hub에 자동 배포
   - `baechoo/community-board:latest`
   - `baechoo/community-board:{commit-sha}`

### 필요한 GitHub Secrets
- `DOCKER_USERNAME`: Docker Hub 사용자명
- `DOCKER_PASSWORD`: Docker Hub 비밀번호

## 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

## 개발 가이드

### 커밋 전 확인사항
1. 모든 테스트가 통과하는지 확인
2. 코드 스타일 검사 통과
3. 빌드가 성공적으로 완료되는지 확인
