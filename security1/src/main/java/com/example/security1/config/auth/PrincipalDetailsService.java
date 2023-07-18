package com.example.security1.config.auth;

import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어 있는 loadUserByUsername 함수가 실행(그냥 규칙임)
@Service //메모리에 띄워줌 IoC등록
public class PrincipalDetailsService implements UserDetailsService {

    // ※ <loginForm.html>에서 input에 name = usernmae2 (x) name = username (0)
    // username2로 할 시, SecurityConfig에서 .usernameParameter("username2")로 해야 매칭 됨.

    @Autowired
    private UserRepository userRepository;

    // 시큐리티 Session = Authentication = UserDetails
    // UserDetails 구현체 : <PrincipalDetails>

    /*
     최종적으로 username이 존재하여 if문을 읽으면
     시큐리티 Session(내부 Authentication(내부 UserDetails)) 이렇게 품고 품어짐
     */
    // 아래 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if(username != null){
            /* UserDetails[PrincipalDetails]가 리턴이 될 때,
                리턴된 값이 Authentication에 들어간다.
                그러면서, 시큐리티 session에 Authentication을 넣어줌

                이 모든 것을 loadUserByUsername이 알아서 다 해줌.
                => 로그인 완료
            */
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
