package mx.edu.uteq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.edu.uteq.model.DetalleOrden;
import mx.edu.uteq.model.Orden;
import mx.edu.uteq.model.Producto;
import mx.edu.uteq.model.Usuario;
import mx.edu.uteq.service.IDetalleOrdenService;
import mx.edu.uteq.service.IOrdenService;
import mx.edu.uteq.service.IUsuarioService;
import mx.edu.uteq.service.ProductoService;

@Controller
@RequestMapping("/")
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private ProductoService productoService;  // Inyecta el servicio ProductoService
	
	@Autowired
	private IUsuarioService usuarioService;  // Inyecta el servicio IUsuarioService
	
	@Autowired
	private IOrdenService ordenService;  // Inyecta el servicio IOrdenService
	
	@Autowired
	private IDetalleOrdenService detalleOrdenService;  // Inyecta el servicio IDetalleOrdenService

	List<DetalleOrden> detalles = new ArrayList<DetalleOrden>();  // Lista de detalles de la orden
	
	Orden orden = new Orden();  // Objeto Orden para almacenar los detalles y la información de la orden
	
	@GetMapping("")
	public String home(Model model, HttpSession session) {
		// Maneja la ruta base "/"
		// Obtiene el ID del usuario de la sesión y lo muestra en el registro
		log.info("Sesion del usuario: {}", session.getAttribute("idusuario"));
		model.addAttribute("productos", productoService.findAll());
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		return "usuario/home";  // Devuelve la vista "usuario/home"
	}
	
	@GetMapping("productohome/{id}")
	public String productoHome(@PathVariable Integer id, Model model) {
		// Maneja la ruta "/productohome/{id}"
		// donde {id} es un parámetro variable en la URL
		log.info("Id producto enviado como parámetro {}", id);
		Producto producto = new Producto();
		Optional<Producto> productoOptional = productoService.get(id);
		producto = productoOptional.get();

		model.addAttribute("producto", producto);
		
		return "usuario/productohome";  // Devuelve la vista "usuario/productohome"
	}

	@PostMapping("/cart")
	public String addCart(@RequestParam Integer id, @RequestParam Integer cantidad, Model model) {
		// Maneja la solicitud POST en "/cart"
		// Agrega un producto al carrito de compras
		DetalleOrden detalleOrden = new DetalleOrden();
		Producto producto = new Producto();
		double sumaTotal = 1;

		Optional<Producto> optionalProducto = productoService.get(id);
		log.info("Producto añadido: {}", optionalProducto.get());
		log.info("Cantidad: {}", cantidad);
		producto = optionalProducto.get();

		detalleOrden.setCantidad(cantidad);
		detalleOrden.setPrecio(producto.getPrecio());
		detalleOrden.setNombre(producto.getNombre());
		detalleOrden.setTotal(producto.getPrecio()*cantidad);
		detalleOrden.setProducto(producto);
		
		Integer idProducto = producto.getId();
		boolean ingresado = detalles.stream().anyMatch(p -> p.getProducto().getId()==idProducto);
		
		if (!ingresado) {
			detalles.add(detalleOrden);
		}
		
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		
		return "usuario/carrito";  // Devuelve la vista "usuario/carrito"
	}

	@GetMapping("/delete/cart/{id}")
	public String deleteProductoCart(@PathVariable Integer id, Model model) {
		// Maneja la eliminación de un producto del carrito de compras
		List<DetalleOrden> ordenesNueva = new ArrayList<DetalleOrden>();

		for (DetalleOrden detalleOrden : detalles) {
			if (detalleOrden.getProducto().getId() != id) {
				ordenesNueva.add(detalleOrden);
			}
		}

		detalles = ordenesNueva;

		double sumaTotal = 0;
		sumaTotal = detalles.stream().mapToDouble(dt -> dt.getTotal()).sum();

		orden.setTotal(sumaTotal);
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);

		return "usuario/carrito";  // Devuelve la vista "usuario/carrito"
	}
	
	@GetMapping("/getCart")
	public String getCart(Model model, HttpSession session) {
		// Obtiene el carrito de compras
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		return "/usuario/carrito";  // Devuelve la vista "/usuario/carrito"
	}
	
	@GetMapping("/order")
	public String order(Model model, HttpSession session) {
		// Procesa la orden de compra
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		
		model.addAttribute("cart", detalles);
		model.addAttribute("orden", orden);
		model.addAttribute("usuario", usuario);
		
		return "usuario/resumenorden";  // Devuelve la vista "usuario/resumenorden"
	}
	
	@GetMapping("/saveOrder")
	public String saveOrder(HttpSession session) {
		// Guarda la orden de compra en la base de datos
		Date fechaCreacion = new Date();
		orden.setFechaCreacion(fechaCreacion);
		orden.setNumero(ordenService.generarNumeroOrden());
		
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		
		orden.setUsuario(usuario);
		ordenService.save(orden);
		
		for (DetalleOrden dt:detalles) {
			dt.setOrden(orden);
			detalleOrdenService.save(dt);
		}
		
		orden = new Orden();
		detalles.clear();
		
		return "redirect:/";  // Redirige a la ruta base "/"
	}
	
	@PostMapping("/search")
	public String searchProduct(@RequestParam String nombre, Model model) {
		// Busca productos por nombre
		log.info("Nombre del producto: {}", nombre);
		List<Producto> productos= productoService.findAll().stream().filter( p -> p.getNombre().contains(nombre)).collect(Collectors.toList());
		model.addAttribute("productos", productos);		
		return "usuario/home";  // Devuelve la vista "usuario/home"
	}

	@GetMapping("/mapa")
	public String mapasitio() {

		return "usuario/mapa";// Devuelve la vista "usuario/mapa"
	}
}
