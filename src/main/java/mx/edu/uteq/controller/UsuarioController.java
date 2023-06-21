package mx.edu.uteq.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import mx.edu.uteq.model.Orden;
import mx.edu.uteq.model.Usuario;
import mx.edu.uteq.service.IOrdenService;
import mx.edu.uteq.service.IUsuarioService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	private final Logger logger= LoggerFactory.getLogger(UsuarioController.class);
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;
	
	//BCryptPasswordEncoder passEncode= new BCryptPasswordEncoder();
	
	@GetMapping("/registro")
	public String create() {
		// Muestra la vista "usuario/registro" para el formulario de registro de usuario
		return "usuario/registro";
	}
	
	@PostMapping("/save")
	public String save(Usuario usuario) {
		// Guarda un nuevo usuario en la base de datos
		
		logger.info("Usuario registro: {}", usuario);
		usuario.setTipo("USER");
		//usuario.setPassword( passEncode.encode(usuario.getPassword()));
		usuarioService.save(usuario);		
		return "redirect:/";  // Redirige a la página principal
	}
	
	@GetMapping("/login")
	public String login() {
		// Muestra la vista "usuario/login" para el formulario de inicio de sesión
		return "usuario/login";
	}
	
	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		// Maneja el inicio de sesión del usuario
		
		logger.info("Accesos : {}", usuario);
		
		Optional<Usuario> user=usuarioService.findByEmail(usuario.getEmail());
		
		if (user.isPresent()) {
			session.setAttribute("idusuario", user.get().getId());
			
			if (user.get().getTipo().equals("ADMIN")) {
				return "redirect:/administrador";  // Redirige al panel de administración
			} else {
				return "redirect:/";  // Redirige a la página principal
			}
		} else {
			logger.info("Usuario no existe");
		}
		
		return "redirect:/";  // Redirige a la página principal si el usuario no existe
	}
	
	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {
		// Obtiene las compras realizadas por el usuario
		
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
		List<Orden> ordenes = ordenService.findByUsuario(usuario);
		logger.info("ordenes {}", ordenes);
		
		model.addAttribute("ordenes", ordenes);
		
		return "usuario/compras";  // Muestra la vista "usuario/compras" con las compras realizadas
	}
	
	@GetMapping("/detalle/{id}")
	public String detalleCompra(@PathVariable Integer id, HttpSession session, Model model) {
		// Muestra el detalle de una compra específica
		
		logger.info("Id de la orden: {}", id);
		Optional<Orden> orden = ordenService.findById(id);
		
		model.addAttribute("detalles", orden.get().getDetalle());
		model.addAttribute("sesion", session.getAttribute("idusuario"));
		
		return "usuario/detallecompra";  // Muestra la vista "usuario/detallecompra" con el detalle de la compra
	}
	
	@GetMapping("/cerrar")
	public String cerrarSesion( HttpSession session ) {
		// Cierra la sesión del usuario
		
		session.removeAttribute("idusuario");
		return "redirect:/";  // Redirige a la página principal
	}
}
