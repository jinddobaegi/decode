package com.decode.web.domain.user.service;

import com.decode.web.domain.user.dto.AuthDto;
import com.decode.web.domain.user.dto.AuthDto.TokenDto;
import com.decode.web.global.utils.authentication.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final String SERVER = "Server";

    @Override
    @Transactional
    public AuthDto.TokenDto login(AuthDto.LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateToken(SERVER, authentication.getName());
    }

    @Override
    public boolean validate(String token) {
        String requestAccessToken = resolveToken(token);
        return jwtTokenProvider.validateTokenExpired(requestAccessToken);

    }

    @Override
    @Transactional
    // 이미 validate 메소드에서 토큰이 유효한지 검사했기 때문에 여기서는 토큰만 검사하면 된다.
    public AuthDto.TokenDto reissue(String principal) {

        TokenDto tokenDto = generateToken(SERVER, principal);
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenDto.getAccessToken());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT(" + SERVER + "):" + principal); // 기존 RT 삭제
        return tokenDto;
    }

    @Override
    @Transactional
    public AuthDto.TokenDto generateToken(String provider, String email) {
        if (redisService.getValues("RT(" + provider + "):" + email) != null) {
            redisService.deleteValues("RT(" + provider + "):" + email);
        }

        AuthDto.TokenDto tokenDto = AuthDto.TokenDto.builder()
                .accessToken(jwtTokenProvider.createAccessToken(email, provider))
                .refreshToken(jwtTokenProvider.createRefreshToken(email, provider))
                .build();
        saveRefreshToken(provider, email, tokenDto.getRefreshToken());
        return tokenDto;
    }

    @Override
    @Transactional
    public void saveRefreshToken(String provider, String principal, String token) {
        redisService.setValuesWithTimeout("RT(:" + provider + "):" + principal,
                token,
                jwtTokenProvider.getTokenExpirationTime(token));
    }

    @Override
    public String getPrincipal(String token) {
        return jwtTokenProvider.getAuthentication(token).getName();
    }

    @Override
    public String resolveToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    @Override
    @Transactional
    public void logout(String token) {
        String requestAccessToken = resolveToken(token);
        String principal = getPrincipal(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT(:" + SERVER + "):" + principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues("RT(:" + SERVER + "):" + principal);
        }
//        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
//        redisService.setValuesWithTimeout(requestAccessToken, "logout", expiration);
    }
}
