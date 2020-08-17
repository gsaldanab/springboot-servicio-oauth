package com.geo.springboot.oauth.app.services;

import com.geo.springboot.usuarios.commons.app.models.entity.Usuario;

public interface IUsuarioService {

	Usuario findByUsername(String username);
	
	Usuario update(Usuario body, Long id);
}
