package com.example.security1.controller;

import com.example.security1.config.auth.PrincipalDetails;
import com.example.security1.model.User;
import com.example.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller //View를 리턴
//@RequiredArgsConstructor
public class IndexController {

    //    private final UserRepository userRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOauthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth) { // DI(의존성 주입)
        System.out.println("/test/login ================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); // 방법1 : down casting
        System.out.println("authentication:" + oAuth2User.getAttributes()); // 방법2 : applying annotation

        System.out.println("oauth2User:"+oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

    @GetMapping("/test/login")
    public @ResponseBody String loginTest(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails) { // DI(의존성 주입)
        System.out.println("/test/login ================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication:" + principalDetails.getUser());

        // @AuthenticationPrincipal 을 통해서 세션 정보에 접근할 수가 있다.
        System.out.println("userDetails = " + userDetails.getUser());
        return "세션 정보 확인하기";

        /*
        ① Authentication을 DI하고 다운캐스팅 과정 거쳐 user 객체 찾을 수 도 있고
        ② @AuthenticationPrincipal을 통해 user 객체 찾을 수도 있다.
         */
    }




    //localhost:8080/
    //localhost:8080
    @GetMapping({"", "/"})
    public String index() {
        //mustache basic folder: src/main/resources/
        //view resolver setting : templates(prefix), .mustache(suffix)
        return "index"; //src/main/resources/templates/index.mustache
    }


    // OAuth 로그인을 해도 PrincipalDetails
    // 일반 로그인을 해도 PrincipalDetails가 되어 따로 분배할 필요 X
    // ※@AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어진다.
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // spring security가 해당 주소를 낚아챈다. <--SecurityConfig 파일 생성 후 작동 안함.
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm") //회원가입 가능 페이지
    public String joinForm() {
        return "joinForm";
    }


    @PostMapping("/join") //회원가입 완료
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");

        //비밀번호 암호화를 해야함(인코딩 시켜서 저장)
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user); //회원가입 잘됨. 비밀번호:1234 => 시큐리티로 로그인을 할 수 없음. 이유는 패스워드가 암호화가 안되었지 때문!!
        return "redirect:/loginForm"; // redirect:/ 붙이면 뒤 url을 호출해줌
    }

    @Secured("ROLE_ADMIN") //특정 메서드에 간단하게 걸고 싶은 권한 부여
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    //이 data() 메소드가 실행되기 직전에 실행됨. (하나만 쓰려면 secured. 여래 개 쓰려면, preAuthorize)
//    @PostAuthorize() //함수가 끝나고 난 뒤 ( 잘 안씀 )
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }

    @PostMapping("/joinProc")
    public String joinProc(User user) {
        System.out.println("회원가입 진행 : " + user);
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "redirect:/";
    }
}
