package com.epam.rd.autocode.spring.project.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final PasswordEncoder passwordEncoder;

    private ClientServiceImpl clientService;
    
    public CustomOAuth2UserService(@Lazy ClientServiceImpl clientService, @Lazy PasswordEncoder passwordEncoder) {
		this.clientService = clientService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        
        try {
        	clientService.getClientByEmail(email);
		} catch (NotFoundException ex) {
			clientService.addClient(
					new ClientDTO(email, passwordEncoder.encode(email), name, BigDecimal.ZERO, false)		
			);
		}
        
        mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_CLIENT"));

        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
    }
}
