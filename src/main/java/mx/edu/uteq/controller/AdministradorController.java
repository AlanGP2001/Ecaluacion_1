package mx.edu.uteq.controller;

import java.util.List;

import org.slf4j.*;  // Importa la clase Logger y sus dependencias
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import mx.edu.uteq.model.Orden;
import mx.edu.uteq.model.Producto;
import mx.edu.uteq.service.IOrdenService;
import mx.edu.uteq.service.IUsuarioService;
import mx.edu.uteq.service.ProductoService;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private ProductoService productoService;  // Inyecta el servicio ProductoService
	
	@Autowired
	private IUsuarioService usuarioService;  // Inyecta el servicio IUsuarioService
	
	@Autowired
	private IOrdenService ordensService;  // Inyecta el servicio IOrdenService
	
	private Logger logg = LoggerFactory.getLogger(AdministradorController.class);  // Crea una instancia del Logger

	@GetMapping("")
	public String home(Model model) {
		// Maneja la ruta base "/administrador"
		// Carga la lista de productos y los agrega al modelo
		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);
		return "administrador/home";  // Devuelve la vista "administrador/home"
	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		// Maneja la ruta "/administrador/usuarios"
		// Carga la lista de usuarios y los agrega al modelo
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";  // Devuelve la vista "administrador/usuarios"
	}
	
	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		// Maneja la ruta "/administrador/ordenes"
		// Carga la lista de órdenes y las agrega al modelo
		model.addAttribute("ordenes", ordensService.findAll());
		return "administrador/ordenes";  // Devuelve la vista "administrador/ordenes"
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
		// Maneja la ruta "/administrador/detalle/{id}"
		// donde {id} es un parámetro variable en la URL
		logg.info("Id de la orden {}",id);  // Imprime un mensaje de registro con el ID de la orden
		Orden orden = ordensService.findById(id).get();  // Recupera la orden correspondiente al ID proporcionado
		model.addAttribute("detalles", orden.getDetalle());  // Agrega los detalles de la orden al modelo
		return "administrador/detalleorden";  // Devuelve la vista "administrador/detalleorden"
	}
}
