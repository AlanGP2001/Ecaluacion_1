package mx.edu.uteq.service;

import java.util.List;
import java.util.Optional;

import mx.edu.uteq.model.Usuario;

public interface IUsuarioService {
	List<Usuario> findAll();
	Optional<Usuario> findById(Integer id);
	Usuario save (Usuario usuario);
	//Usuario findByCorreo(String email);
	Optional<Usuario> findByEmail(String email);
}
