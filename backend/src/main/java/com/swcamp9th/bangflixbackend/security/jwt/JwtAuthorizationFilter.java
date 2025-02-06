package com.swcamp9th.bangflixbackend.security.jwt;

import com.swcamp9th.bangflixbackend.shared.util.JwtUtil;
import com.swcamp9th.bangflixbackend.security.user.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
		String tokenValue = jwtUtil.getTokenFromRequest(req);
		log.debug("tokenValue: {}", tokenValue);
		if (StringUtils.hasText(tokenValue)) {
//			tokenValue = jwtUtil.substringToken(tokenValue);

			if (!jwtUtil.validateAcessToken(tokenValue)) {
				return;
			}

			Claims info = jwtUtil.getAccessTokenClaims(tokenValue);
			setAuthentication(info.getSubject());
		}

		filterChain.doFilter(req, res);
	}

	private void setAuthentication(String username) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Authentication authentication = createAuthentication(username);
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}