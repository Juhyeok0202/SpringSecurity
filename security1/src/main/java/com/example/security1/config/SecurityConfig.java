package com.example.security1.config;

import com.example.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration //메모리에 떠야하니
@EnableWebSecurity //활성화를 시킨다-> 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨.
@EnableGlobalMethodSecurity(securedEnabled = true , prePostEnabled = true) // sercured 어노테이션 활성화 , preAuthorize+postAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    // @Bean 통해서 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
//    @Bean
//    public BCryptPasswordEncoder encodePwd() { //패스워드 암호화시키기 위해서
//        return new BCryptPasswordEncoder();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // ⭐restful 같은 경우는 할 필요 없다고 하는데, 더 알아봐야 겠음.
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // user/** 로 들어가려면 인증 필요
                // manager/**는 로그인은 했지만 해당 권한이 있어야 들어올 수 있다.
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll() //나머지 주소는 모두 권한 허용

                // login 필요한 url요청 시, 해당 url로 무조건 이동하도록 함.


                // 아래 url에 로그인 페이지 구현하면 됨.
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // /login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다. -> 컨트롤러에 /login 안만들어도 시큐리티가 대신 함
                .defaultSuccessUrl("/")    //로그인 성공 시 디폴트 페이지 메인 페이지로 이동
                .and()
                .oauth2Login()
                .loginPage("/loginForm") //구글 로그인이 완료된 뒤의 후처리가 필요함.(7강부터 학습)함-
                //구글 로그인이 완료된 뒤의 후처리가 필요함. ⭐TIP⭐코드를 받는게 X, (액세스 토큰 + 시용자 프로필 정보 O)
                .userInfoEndpoint()
                .userService(principalOauth2UserService); //Oauth2UserService 타입이 인자로 들어가야함

    }
}
