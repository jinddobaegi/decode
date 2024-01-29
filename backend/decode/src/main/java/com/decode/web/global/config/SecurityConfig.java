package com.decode.web.global.config;


import com.decode.web.domain.user.service.AuthService;
import com.decode.web.global.filter.JwtAuthenticationFilter;
import com.decode.web.global.utils.authentication.JwtAccessDeniedHandler;
import com.decode.web.global.utils.authentication.JwtAuthenticationEntryPoint;
import com.decode.web.global.utils.authentication.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final CorsConfig corsconfig;
    private final AuthService authservice;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/**", "/decode/**").permitAll()
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(httpBasic ->
                        httpBasic.disable()
                )
                .csrf(csrf ->
                        csrf.disable()
                )

                .logout(logout ->
                        logout.disable()
                )
                .formLogin(formLogin ->
                        formLogin.disable()
                )
//                .oauth2Login(oauth2Login ->
//                        oauth2Login.userInfoEndpoint(userInfoEndpoint ->
//                                userInfoEndpoint.userService()
//                        )
//                )
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .addFilter(corsconfig.corsFilter())
                .addFilterBefore(new JwtAuthenticationFilter(authservice, jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}