package com.swcamp9th.bangflixbackend.security.user;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.entity.MemberRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

	private final Member member;

	public UserDetailsImpl(Member member) {
		this.member = member;
	}

	public Member getUser() { return member; }

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		MemberRoleEnum role = member.getRole();
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getAuthority());
		return Collections.singletonList(authority);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}