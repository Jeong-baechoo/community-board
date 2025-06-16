# Community Board - DDD 기반 게시판 프로젝트

## 프로젝트 구조

```
src/main/java/com/example/communityboard/
├── domain/           # 도메인 계층
│   ├── member/      # 회원 도메인
│   ├── board/       # 게시판 도메인
│   └── comment/     # 댓글 도메인
├── application/     # 응용 계층
├── infrastructure/  # 인프라 계층
└── presentation/    # 표현 계층
```

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

## 결론

DDD는 완벽한 설계보다는 **도메인을 잘 표현하는 설계**가 중요합니다. 
기술적 제약(JPA)과 도메인 순수성 사이에서 적절한 균형을 찾는 것이 핵심입니다.