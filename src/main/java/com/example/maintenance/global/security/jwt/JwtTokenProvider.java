package com.example.maintenance.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.example.maintenance.domain.user.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final long accessTokenValidityInMilliseconds;

	public JwtTokenProvider(JwtProperties jwtProperties) {
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
}