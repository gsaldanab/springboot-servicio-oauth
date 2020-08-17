package com.geo.springboot.oauth.app.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;

/*
 * Clase que sobreescribe la configuracion por defecto authenticationEventPublisher
 * de esta forma el authenticationManager no implementará el authenticationEventPublisher creado 
 * en la clase AuthenticationSuccessErrorHandler
 * 
 * Al crear la clase AuthenticationSuccessErrorHandler Spring automaticamente incorpora el filtro 
 * en el authenticationManager lo cual hace que se verifique las credenciales del cliente. 
 * Esta clase lo impide para que solo se agregue en la authenticacion del usuario.
 * 
 * 
 * if (eventPublisher != null) {
 * 	providerManager.setAuthenticationEventPublisher(eventPublisher);
 * }	
 *
 */
@Configuration
@Order(-1)
public class BasicSecurityConfiguration extends AuthorizationServerSecurityConfiguration {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    super.configure(http);

	    // override default DefaultAuthenticationEventPublisher to avoid excessive firing of
	    // AuthenticationSuccessEvent on successful client credentials verification that passed in "Authorization: Basic clientId:clientSecret" header
	    http.getSharedObject(AuthenticationManagerBuilder.class).authenticationEventPublisher(new NullEventPublisher());
	    http.httpBasic();
	}

	private static final class NullEventPublisher implements AuthenticationEventPublisher {

		@Override
		public void publishAuthenticationSuccess(Authentication authentication) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
			// TODO Auto-generated method stub
			
		}
	   
	}
	}