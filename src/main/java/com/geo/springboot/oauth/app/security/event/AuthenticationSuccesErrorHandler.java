package com.geo.springboot.oauth.app.security.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.geo.springboot.oauth.app.services.IUsuarioService;
import com.geo.springboot.usuarios.commons.app.models.entity.Usuario;

import feign.FeignException;

@Component
public class AuthenticationSuccesErrorHandler implements AuthenticationEventPublisher {

	private Logger log = LoggerFactory.getLogger(AuthenticationSuccesErrorHandler.class);

	@Autowired
	private IUsuarioService usuarioService;

	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		UserDetails user = (UserDetails) authentication.getPrincipal();
		String mensaje = String.format("Success Loggin: %s", user.getUsername());
		System.out.println(mensaje);
		log.info(mensaje);
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		if(usuario.getIntentos() != null && usuario.getIntentos() > 0) {
			usuario.setIntentos(0);
			usuarioService.update(usuario, usuario.getId());
		}
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		String mensaje = String.format("Login con error: %s", exception.getMessage());
		System.out.println(mensaje);
		log.info(mensaje);

		try {
			Usuario usuario = usuarioService.findByUsername(authentication.getName());
			if (usuario.getIntentos() == null) {
				usuario.setIntentos(0);
			}
			log.info("Intento actual es: {}", usuario.getIntentos());
			usuario.setIntentos(usuario.getIntentos() + 1);
			log.info("Intento despues es: {}", usuario.getIntentos());

			if (usuario.getIntentos() >= 3) {
				log.error("El usuario {} ha sido deshabilitado por superar el maximo de intentos",
						usuario.getUsername());
				usuario.setEnabled(false);
			}

			usuarioService.update(usuario, usuario.getId());
		} catch (FeignException e) {
			log.error(String.format("El usuario %s no existe en el sistema", authentication.getName()));
		}

	}

}
