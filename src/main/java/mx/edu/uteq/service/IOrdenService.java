package mx.edu.uteq.service;

import java.util.List;
import java.util.Optional;

import mx.edu.uteq.model.Orden;
import mx.edu.uteq.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
	Optional<Orden> findById(Integer id);
	Orden save (Orden orden);
	String generarNumeroOrden();
	List<Orden> findByUsuario (Usuario usuario);
}
