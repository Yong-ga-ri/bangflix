package com.swcamp9th.bangflixbackend.config;

import com.swcamp9th.bangflixbackend.shared.filter.JwtExceptionFilter;
import com.swcamp9th.bangflixbackend.security.jwt.JwtAuthorizationFilter;
import com.swcamp9th.bangflixbackend.shared.util.JwtUtil;
import com.swcamp9th.bangflixbackend.security.user.UserDetailsServiceImpl;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;


    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public Filter jwtAuthorizationFilter() {
		return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
	}

	public Filter jwtExceptionFilter() {
		return new JwtExceptionFilter();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);

		http.cors(corsConfig -> corsConfig.getClass());

		http.formLogin(AbstractHttpConfigurer::disable);

		http.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		);

		http.authorizeHttpRequests(authorize ->
				authorize
						.requestMatchers("/api/v1/**").permitAll()
//						.requestMatchers("/api/v1/auth/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/themes/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/stores/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/community/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/event/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/notice/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/community-like/**").permitAll()
						.requestMatchers("/uploadFiles/**").permitAll()
						.anyRequest().authenticated()
		);

		http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtExceptionFilter(), jwtAuthorizationFilter().getClass());

		return http.build();
	}
}