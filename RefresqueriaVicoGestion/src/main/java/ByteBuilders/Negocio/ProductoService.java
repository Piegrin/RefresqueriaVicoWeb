package ByteBuilders.Negocio;

import ByteBuilders.Entidad.*;
import ByteBuilders.Persistencia.*;
import java.util.*;
import java.util.regex.Pattern;

public class ProductoService {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    public List<Producto> listarProductos() {
        return productoDAO.obtenerTodos();
    }

    public Producto obtenerPorID(int id) {
        return productoDAO.obtenerPorID(id);
    }

    // Letras (con acentos y ñ), números, espacios y puntuación básica
    private static final Pattern REGEX_NOMBRE =
            Pattern.compile("^[\\p{L}\\p{N} .,&'\\-]{1,100}$");
    private static final Pattern REGEX_DESCRIPCION =
            Pattern.compile("^[\\p{L}\\p{N} .,;:!?()&'\\-\\r\\n]{0,500}$");

    /**
     * Valida y registra un producto nuevo.
     * @throws IllegalArgumentException con el motivo si alguna validación falla
     */
    public boolean registrarProducto(Producto p) {
        validarAlta(p);
        return productoDAO.insertar(p);
    }

    private void validarAlta(Producto p) {
        if (p == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }

        // Nombre: obligatorio, caracteres permitidos
        String nombre = p.getNombre() == null ? "" : p.getNombre().trim();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (!REGEX_NOMBRE.matcher(nombre).matches()) {
            throw new IllegalArgumentException("El nombre contiene caracteres no permitidos o excede 100 caracteres.");
        }

        // Descripción: opcional, caracteres permitidos
        String descripcion = p.getDescripcion();
        if (descripcion != null && !REGEX_DESCRIPCION.matcher(descripcion).matches()) {
            throw new IllegalArgumentException("La descripción contiene caracteres no permitidos o excede 500 caracteres.");
        }

        // Precio e inventario
        if (p.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        if (p.getInventario() < 0) {
            throw new IllegalArgumentException("El inventario debe ser mayor o igual a 0.");
        }

        // Categoría: debe existir en la base de datos
        if (p.getCategoria() == null || p.getCategoria().getCategoriaID() <= 0) {
            throw new IllegalArgumentException("Debes seleccionar una categoría válida.");
        }
        Categoria categoria = categoriaDAO.obtenerPorID(p.getCategoria().getCategoriaID());
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría seleccionada no existe.");
        }

        // Nombre único (sin distinguir mayúsculas)
        for (Producto existente : productoDAO.obtenerTodos()) {
            if (existente.getNombre() != null && existente.getNombre().trim().equalsIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Ya existe un producto con el nombre \"" + nombre + "\".");
            }
        }

        // Normaliza: nombre sin espacios sobrantes y categoría real de la BD
        p.setNombre(nombre);
        p.setCategoria(categoria);
    }

    public boolean actualizarStock(int productoID, int nuevaCantidad) {
        Producto p = productoDAO.obtenerPorID(productoID);
        if (p == null) {
            System.out.println("❌ Producto no encontrado.");
            return false;
        }
        if (nuevaCantidad < 0) {
            System.out.println("⚠️ No se puede asignar stock negativo.");
            return false;
        }
        p.setInventario(nuevaCantidad);
        return productoDAO.actualizar(p);
    }

    public boolean agregarProducto(Producto producto) {
        return registrarProducto(producto);
    }

    public boolean actualizarProducto(Producto producto) {
        if (producto == null || producto.getProductoID() <= 0) return false;
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            System.out.println("Error: El nombre del producto es obligatorio.");
            return false;
        }
        if (producto.getPrecio() <= 0) {
            System.out.println("Error: El precio debe ser mayor a 0.");
            return false;
        }
        return productoDAO.actualizar(producto);
    }

    public boolean eliminarProducto(int id) {
        return productoDAO.eliminar(id);
    }

    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }
}