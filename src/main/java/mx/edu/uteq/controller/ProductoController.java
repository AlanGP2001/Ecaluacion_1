package mx.edu.uteq.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import mx.edu.uteq.model.Producto;
import mx.edu.uteq.model.Usuario;
import mx.edu.uteq.service.IUsuarioService;
import mx.edu.uteq.service.ProductoService;
import mx.edu.uteq.service.UploadFileService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;  // Inyecta el servicio ProductoService
	
	@Autowired
	private IUsuarioService usuarioService;  // Inyecta el servicio IUsuarioService
	
	@Autowired
	private UploadFileService upload;  // Inyecta el servicio UploadFileService para manejar la subida de archivos
	
	@GetMapping("")
	public String show(Model model) {
		// Muestra todos los productos en la vista "productos/show"
		model.addAttribute("productos", productoService.findAll());
		return "productos/show";
	}
	
	@GetMapping("/create")
	public String create() {
		// Muestra la vista "productos/create" para crear un nuevo producto
		return "productos/create";
	}
	
	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {
		// Guarda un nuevo producto en la base de datos
		
		LOGGER.info("Este es el objeto producto {}",producto);
		
		Usuario u = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString() )).get();
		producto.setUsuario(u);	
		
		if (producto.getId()==null) {
			String nombreImagen = upload.saveImage(file);  // Guarda la imagen en el servidor
			producto.setImagen(nombreImagen);
		}else {
			// El producto ya tiene un ID, se podría manejar un caso específico si se desea
		}
		
		productoService.save(producto);  // Guarda el producto en la base de datos
		return "redirect:/productos";  // Redirige a la página de visualización de productos
	}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Integer id, Model model) {
		// Muestra la vista "productos/edit" para editar un producto existente
		Producto producto = new Producto();
		Optional<Producto> optionalProducto = productoService.get(id);
		producto = optionalProducto.get();

		LOGGER.info("Producto buscado: {}",producto);
		model.addAttribute("producto", producto);
		
		return "productos/edit";
	}
	
	@PostMapping("/update")
	public String update(Producto producto, @RequestParam("img") MultipartFile file ) throws IOException {
		// Actualiza un producto existente en la base de datos
		
		Producto p = new Producto();
		p = productoService.get(producto.getId()).get();
		
		if (file.isEmpty()) {
			producto.setImagen(p.getImagen());  // Conserva la imagen anterior si no se selecciona una nueva
		} else {
			if (!p.getImagen().equals("default.jpg")) {
				upload.deleteImage(p.getImagen());  // Elimina la imagen anterior si no es la imagen por defecto
			}
			String nombreImagen = upload.saveImage(file);  // Guarda la nueva imagen en el servidor
			producto.setImagen(nombreImagen);
		}
		producto.setUsuario(p.getUsuario());  // Conserva el usuario asociado al producto
		
		productoService.update(producto);  // Actualiza el producto en la base de datos
		return "redirect:/productos";  // Redirige a la página de visualización de productos
	}
	
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		// Elimina un producto de la base de datos
		
		Producto p = new Producto();
		p = productoService.get(id).get();
		
		if (!p.getImagen().equals("default.jpg")) {
			upload.deleteImage(p.getImagen());  // Elimina la imagen del servidor si no es la imagen por defecto
		}
		productoService.delete(id);  // Elimina el producto de la base de datos
		return "redirect:/productos";  // Redirige a la página de visualización de productos
	}
}
