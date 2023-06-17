package mx.edu.uteq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.edu.uteq.model.Producto;

@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {

}
