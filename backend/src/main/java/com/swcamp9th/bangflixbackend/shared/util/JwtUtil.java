package com.swcamp9th.bangflixbackend.shared.util;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public final String AUTHORITIES_KEY = "auth";
	public static final String BEARER_PREFIX = "Bearer ";

	@Value("${access-token.expiration_time}")
	private Long accessTokenTime;
	@Value("${refresh-token.expiration_time}")
	private Long refreshTokenTime;
	@Value("${access-token.secret}")
	private String accessTokenSecret;
	@Value("${refresh-token.secret}")
	private String refreshTokenSecret;

	private Key accessTokenKey;
	private Key refreshTokenKey;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] acessBytes = Base64.getDecoder().decode(accessTokenSecret);
		byte[] refreshBytes = Base64.getDecoder().decode(refreshTokenSecret);
		accessTokenKey = Keys.hmacShaKeyFor(acessBytes);
		refreshTokenKey = Keys.hmacShaKeyFor(refreshBytes);
	}

	public String createAccessToken(Member member) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(member.getId())
			.claim(AUTHORITIES_KEY, member.getRole())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + accessTokenTime))
			.signWith(accessTokenKey, signatureAlgorithm)
			.compact();
	}

	public String createRefreshToken(Member member) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(member.getId())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + refreshTokenTime))
			.signWith(refreshTokenKey, signatureAlgorithm)
			.compact();
	}

	public void addJwtToHeader(String token, HttpServletResponse response) {
		response.setHeader(AUTHORIZATION_HEADER, token);
	}

	public boolean validateAcessToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException e) {
			log.info("Invalid JWT signature.");
			throw new JwtException("잘못된 JWT 시그니처입니다.");
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
			throw new JwtException("유효하지 않은 JWT 토큰입니다.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
			throw new JwtException("엑세스 토큰이 만료되었습니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
			throw new JwtException("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
			throw new JwtException("핸들러의 JWT 토큰 컴팩트가 잘못되었습니다.");
		} catch (SignatureException e) {
			throw new JwtException("잘못된 JWT 토큰이 입력되었습니다.");
		}
	}

	public boolean validateRefreshToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(refreshTokenKey).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException e) {
			log.info("Invalid JWT signature.");
			throw new JwtException("잘못된 JWT 시그니처입니다.");
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
			throw new JwtException("유효하지 않은 JWT 토큰입니다.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
			throw new JwtException("리프레시 토큰이 만료되었습니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
			throw new JwtException("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
			throw new JwtException("핸들러의 JWT 토큰 컴팩트가 잘못되었습니다.");
		} catch (SignatureException e) {
			throw new JwtException("잘못된 JWT 토큰이 입력되었습니다.");
		}
    }

	public Claims getAccessTokenClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(accessTokenKey).build().parseClaimsJws(token).getBody();
	}

	public Claims getRefreshTokenClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(refreshTokenKey).build().parseClaimsJws(token).getBody();
	}

	public String getTokenFromRequest(HttpServletRequest req) {
		String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
		log.debug("bearerToken in getTokenFromRequest: {}", bearerToken);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}



	public String getSubjectFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(accessTokenSecret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		}
		throw new NullPointerException("토큰을 찾을 수 없습니다.");
	}
}