package com.geo.springboot.oauth.app.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.geo.springboot.usuarios.commons.app.models.entity.Usuario;

/*
 * Cliente Feign que permite la comunicacion entre microservicios a traves de sus endpoints
 */
@FeignClient(name = "servicio-usuarios")
public interface UsuarioFeignClient {

	@GetMapping("/usuarios/search/findUsername")
	public Usuario findByUsername(@RequestParam String user);
	
	@PutMapping("/usuarios/{id}")
	public Usuario update(@RequestBody Usuario body, @PathVariable Long id);
	
}
