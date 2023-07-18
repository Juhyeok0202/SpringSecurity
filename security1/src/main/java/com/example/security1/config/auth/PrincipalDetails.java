package com.example.security1.config.auth;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인 진행이 완료가 되면 시큐리티 session을 만들어줍니다.(Security ContextHolder;시큐리티 자신만의 세션 공간)
// 시큐리티 세션에 들어갈 수 있는 오브젝트 => Authentication 타입 객체
// Authentication 안에는 User정보가 있어야 됨.
// User오브젝트타입 => UserDetails 타입 객체

// Security Session(여기 세션정보 저장) => Authentication => UserDetails(PrincipalDetails)

import com.example.security1.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/*얘는 왜 메모리에 등록을 하지 않을까? => 나중에 new해서 강제로 띄울 것임*/
@Data
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {
    /*
    일반로그인 -> UserDetails
    OAuth로그인 -> OAuth2User 타입이 Authentication에 들어간다.

    따라서, 이 두 가지는 PrincipalDetils 타입만 찾으면 되도록 implementation해 묶어준다.
     */
    private User user; //콤포지션(유저 정보는 User가 가지고 있음)
    private Map<String,Object> attributes;

    // 일반 로그인 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth 로그인 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //해당 User의 권한을 리턴하는 곳!!!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    //사용자의 계정이 만료되었는지 여부
    // true: 만료 X
    // false: 만료 O. 더 이상 계정이 유효하지 않음
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨 있는지 여부를 반환
    // true: unLocked
    // false: locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //사용자의 자격 증명(비밀번호)가 만료되었는지 여부 반환
    // true: 만료 X
    // false: 만료 O 더 이상 자격 유효 하지 않음
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자 계정이 활성화(사용 가능?)여부를 반환
    // true: 활성화 상태
    // false: 비활성화 상태
    @Override
    public boolean isEnabled() {

        /*false 리턴 사용 예시: 휴먼 계정*/
        // 우리 사이트!!1년동안 회원이 로그인을 안하면!! 휴먼 계정으로 하기로 함.
        // 현재시간 - 로긴시간 => 1년 초과하면 return false;
//        user.getLoginDate();


        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
