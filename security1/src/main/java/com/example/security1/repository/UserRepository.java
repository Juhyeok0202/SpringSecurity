package com.example.security1.repository;

import com.example.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// CRUD 함수를 JpaRepo가 들고 있음
// @Repository 라는 어노테이션이없어도 IoC되요. 이유는 JpaRepository를 상속해서
// 이제 Bean등록 됐으니, 필요한 곳에서 @AutoWired해주면 됨.
public interface UserRepository extends JpaRepository<User, Long> {

    // findBy(규칙) -> Username(문법)
    //Therefore!!!! 다음과 같은 쿼리가 호출 됨.

    /*
    SELECT *
    FROM USER
    WEHRE username = 1? (입력 받은 파라미터 값)
     */

    // ※더 공부해보고 싶으면, [JPA query method] 문법 공부
    public User findByUsername(String username); //Jpa Query Method

}
