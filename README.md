# damoim
spring boot 미니 프로젝트용으로 기능보다는 현재 나의 코드의 습관 및 패키지의 구조를 보여주기 위한 용도의 프로젝트이다.
따라서 평소에 코드 짜는 습관 혹은 패키지 구조를 담당하는 습관 등에 초점을 두면 좋을 것 같다.

대략적인 구조
- advice: ControllerAdvice에 관련된 클래스를 담당. 현재 exception과 관련된 내용을 담고있다.
- config: Configuration에 관련된 클래스를 담당.
- controller: Controller 클래스를 담당.
- dto: DTO에 관련된 클래스를 담당. 주로 Request와 Response 관련 DTO가 주를 이룬다.
- entity: JPA와 관련된 DB 맵핑 클래스를 담당.
- filter: SpringSecurity의 filter에 관련된 클래스를 담당.
- jwt: JWT와 관련된 클래스를 담당. 현재 토큰 생성과 검증을 담당하는 클래스를 담고있다.
- repository: Repository 인터페이스를 담당.
- response: 공통 응답에 관련된 클래스를 담당.
- security: SpringSecurity의 커스텀한 설정 클래스를 담당. 현재 JWT로 인증과 권한을 맡고 있기 때문에 JWT의 인증과 권한에 관련된 클래스를 담고있다.
- service: Service 클래스를 담당.

사용 라이브러리
- Spring Data JPA
- Spring Security
- Spring Validation
- Flyway
- Lombok
- JUnit
- Jackson Databind (ObjectMapper용)
- Swagger 3 (springdoc)
- jjwt
