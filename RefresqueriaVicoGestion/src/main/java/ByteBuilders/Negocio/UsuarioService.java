package ByteBuilders.Negocio;

import ByteBuilders.Entidad.Usuario;
import ByteBuilders.Persistencia.UsuarioDAO;
import java.util.List;
import java.util.regex.Pattern;

public class UsuarioService {

    private static final Pattern PATRON_CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^[0-9+\\-\\s]{7,20}$");

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario obtenerPorID(int id) {
        return usuarioDAO.obtenerPorID(id);
    }

    public boolean crearUsuario(Usuario usuario) {
        if (usuario == null) {
            System.out.println("Error: Usuario no puede ser nulo.");
            return false;
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            System.out.println("Error: El nombre del usuario es obligatorio.");
            return false;
        }

        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            System.out.println("Error: El correo del usuario es obligatorio.");
            return false;
        }

        if (usuario.getContrasenaHash() == null || usuario.getContrasenaHash().trim().isEmpty()) {
            System.out.println("Error: La contrasena del usuario es obligatoria.");
            return false;
        }

        return usuarioDAO.insertar(usuario);
    }

    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            System.out.println("Error: ID de usuario invalido o usuario nulo.");
            return false;
        }

        Usuario existente = usuarioDAO.obtenerPorID(usuario.getIdUsuario());
        if (existente == null) {
            System.out.println("Error: No se puede actualizar porque el usuario no existe.");
            return false;
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            System.out.println("Error: El nombre del usuario es obligatorio.");
            return false;
        }

        if (usuario.getCorreo() == null || !PATRON_CORREO.matcher(usuario.getCorreo().trim()).matches()) {
            System.out.println("Error: Formato de correo invalido.");
            return false;
        }

        return usuarioDAO.actualizar(usuario);
    }

    public boolean eliminarUsuario(int id) {
        if (id <= 0) {
            System.out.println("Error: ID invalido para eliminar usuario.");
            return false;
        }

        Usuario usuario = usuarioDAO.obtenerPorID(id);
        if (usuario == null) {
            System.out.println("Error: No se puede eliminar porque el usuario no existe.");
            return false;
        }

        return usuarioDAO.eliminar(id);
    }

    public String validarCredencialesEntrada(String identificador, String contrasena) {
        if (identificador == null || identificador.trim().isEmpty()) {
            return "Debes ingresar tu correo electronico o numero telefonico.";
        }
        String limpio = identificador.trim();
        boolean esCorreo = PATRON_CORREO.matcher(limpio).matches();
        boolean esTelefono = PATRON_TELEFONO.matcher(limpio).matches();
        if (!esCorreo && !esTelefono) {
            return "Ingresa un correo electronico valido o un numero telefonico de 7 a 20 digitos.";
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            return "Debes ingresar tu contrasena.";
        }
        return null;
    }

    public Usuario iniciarSesion(String identificador, String contrasena) {
        String errorValidacion = validarCredencialesEntrada(identificador, contrasena);
        if (errorValidacion != null) {
            System.out.println("Validacion login fallida: " + errorValidacion);
            return null;
        }
        return usuarioDAO.obtenerPorCredenciales(identificador.trim(), contrasena);
    }
}
