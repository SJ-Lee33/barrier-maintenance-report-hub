package com.example.maintenance.global.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.maintenance.domain.user.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(
			new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
		);
	}

	/**
	 * role 권한은
	 * ROLE_TECHNICIAN / ROLE_MANAGER / ROLE_ADMIN
	 * 이렇게 저장한다! 그래야 .hasRole("ADMIN") 이렇게 쓸 수 있음
	 * */

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	public Long getUserId() {
		return user.getId();
	}

	public String getName() {
		return user.getName();
	}

	public String getEmail() {
		return user.getEmail();
	}

	public String getPhone() {
		return user.getPhone();
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