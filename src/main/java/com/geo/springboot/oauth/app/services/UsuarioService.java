package com.geo.springboot.oauth.app.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.geo.springboot.oauth.app.clients.UsuarioFeignClient;
import com.geo.springboot.usuarios.commons.app.models.entity.Usuario;

import brave.Tracer;
import feign.FeignException;

/*
 * Clase service que permite autentica según el username indicado
 */
@Service
public class UsuarioService implements UserDetailsService, IUsuarioService {

	private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
	
	/*
	 * Utiliza el cliente Feign para comunicarse con el microservicio usuarios
	 */
	@Autowired
	private UsuarioFeignClient client;
	
	@Autowired
	private Tracer tracer;
	
	/*
	 * Método sobrescrito propio de spring framework que permite obtener el usuario por username
	 * - se llama al microservicio usuarios para que retorne la info del usuario 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			Usuario usuario = client.findByUsername(username);
				
			List<GrantedAuthority> authorities = usuario.getRoles()
					.stream()
					.map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
					.peek(authority -> log.info("***Role: "+ authority.getAuthority()))
					.collect(Collectors.toList());
			log.info("Usuario autenticado: {}", username);
			
			/*
			 * Este metodo devuelve un objeto User que contiene informacion del usuario y sus roles
			 */
			return new User(usuario.getUsername(), 
					usuario.getPassword(), 
					usuario.getEnabled(), 
					true, 
					true, 
					true, 
					authorities);
		} catch (FeignException e) {
			String errorMessage = String.format("Error en el login, No existe el usuario %s en el sistema", username);
			log.error(errorMessage);
			tracer.currentSpan().tag("error.mensaje", errorMessage + ":" + e.getMessage());
			throw new UsernameNotFoundException(errorMessage);
		}
	}

	@Override
	public Usuario findByUsername(String username) {
		Usuario usuario = client.findByUsername(username);	
		return usuario;
	}

	@Override
	public Usuario update(Usuario usuario, Long id) {
		return client.update(usuario, id);
	}
	
}
