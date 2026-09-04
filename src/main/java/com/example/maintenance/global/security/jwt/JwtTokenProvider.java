package com.example.maintenance.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.example.maintenance.domain.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenValidityInMilliseconds;

	public JwtTokenProvider(JwtProperties jwtProperties) {
		if (jwtProperties.secret() == null || jwtProperties.secret().isBlank()) {
			throw new IllegalStateException("JWT_SECRET 환경변수가 설정되지 않았습니다.");
		}

		if (jwtProperties.secret().getBytes(StandardCharsets.UTF_8).length < 32) {
			throw new IllegalStateException("JWT_SECRET은 최소 32바이트 이상이어야 합니다.");
		}

		this.secretKey = Keys.hmacShaKeyFor(
			jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
		);
		this.accessTokenValidityInMilliseconds =
			jwtProperties.accessTokenValidityInMilliseconds();
	}

	public String createAccessToken(User user) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + accessTokenValidityInMilliseconds);

		return Jwts.builder()
			.subject(String.valueOf(user.getId()))
			.claim("email", user.getEmail())
			.claim("role", user.getRole().name())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey)
			.compact();
	}

	// 정상 토큰인지 확인
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

	// JWT의 subject에서 userId 꺼내기
	public Long getUserId(String token) {
		Claims claims = parseClaims(token);
		return Long.valueOf(claims.getSubject());
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}