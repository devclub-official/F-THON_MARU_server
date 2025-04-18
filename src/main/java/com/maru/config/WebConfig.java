package com.maru.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 이 클래스가 Spring 설정 클래스임을 나타냅니다.
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 애플리케이션의 모든 경로("/**")에 대해 CORS 설정을 적용합니다[1][3][4].
                .allowedOriginPatterns("*") // 모든 출처(Origin)에서의 요청을 허용합니다[1][4]. 이것이 모든 호스트를 허용하는 설정입니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP 메소드를 지정합니다. "*"로 모든 메소드를 허용할 수도 있습니다[1][3][4].
                .allowedHeaders("*") // 모든 요청 헤더를 허용합니다[3][7].
                .allowCredentials(true) // 쿠키나 인증 정보와 같은 자격 증명(Credentials)을 포함한 요청을 허용할지 여부를 설정합니다[1][3][4]. true 설정 시 보안에 유의해야 합니다[4].
                .maxAge(3600); // Pre-flight 요청의 결과를 캐시할 시간을 초 단위로 설정합니다[3][4].
    }
}