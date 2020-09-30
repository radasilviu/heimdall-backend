package com.antonio.authserver.service;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import com.antonio.authserver.dto.AppUserDto;
import com.antonio.authserver.model.CustomException;
import com.antonio.authserver.utils.SecurityConstants;
import io.jsonwebtoken.*;

@Service
public class JwtService {

	private Environment env;
	private UserService userService;

	@Autowired
	public JwtService(Environment env, UserService userService) {
		this.env = env;
		this.userService = userService;
	}

	public String createAccessToken(String issuer, long expirationTime,
			Collection<? extends GrantedAuthority> authorities, String secretKey) {
		final AppUserDto userDto = userService.getUserByUsername(issuer);

		String token = buildToken(userDto.getUsername(), userDto.toString(), expirationTime, authorities, secretKey);

		return token;
	}

	private String buildToken(String issuer, String subject, long expirationTime,
			Collection<? extends GrantedAuthority> authorities, String secretKey) {
		String token = Jwts.builder().setIssuer(issuer).setSubject(subject).claim("authorities", authorities)
				.setExpiration(new Date(expirationTime)).signWith(SignatureAlgorithm.HS512, secretKey).compact();

		return token;
	}

	public Claims decodeJWT(String jwt) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(SecurityConstants.TOKEN_SECRET).parseClaimsJws(jwt).getBody();
		} catch (ExpiredJwtException e) {
			throw new CustomException("Token [ " + jwt + " ] expired!", HttpStatus.UNAUTHORIZED);
		} catch (JwtException ex) {
			throw new CustomException("Token [ " + jwt + " ] can not be trusted", HttpStatus.UNAUTHORIZED);
		}

		return claims;
	}

	public String createRefreshToken(Long expirationTime, String secretKey) {

		String refreshToken = Jwts.builder().setExpiration(new Date(expirationTime))
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();

		return refreshToken;

	}
}
