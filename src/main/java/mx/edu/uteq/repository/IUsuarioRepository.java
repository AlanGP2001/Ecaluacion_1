package mx.edu.uteq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.model.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
	Optional<Usuario> findByEmail(String email);

	//@Query("SELECT u FROM Usuario u WHERE u.email = :email ")
	//Usuario findByCorreo(String email);
}
