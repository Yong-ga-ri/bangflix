package com.swcamp9th.bangflixbackend.domain.user.dto;

import com.swcamp9th.bangflixbackend.domain.entity.Member;
import lombok.Getter;

@Getter
public class SignupResponseDto {

	private final String id;
	private final String nickname;
	private final String authority;

	public SignupResponseDto(Member member) {
		this.id = member.getId();
		this.nickname = member.getNickname();
		this.authority = member.getRole().getAuthority();
	}
}