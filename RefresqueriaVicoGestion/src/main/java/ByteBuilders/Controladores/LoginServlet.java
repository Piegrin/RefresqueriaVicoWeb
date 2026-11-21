package ByteBuilders.Controladores;

import ByteBuilders.Negocio.UsuarioService;
import ByteBuilders.Entidad.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private final UsuarioService usuarioService = new UsuarioService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            JsonObject credenciales = gson.fromJson(request.getReader(), JsonObject.class);
            if (credenciales == null) {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Solicitud de inicio de sesion vacia");
                return;
            }

            String identificador = "";
            if (credenciales.has("identificador") && !credenciales.get("identificador").isJsonNull()) {
                identificador = credenciales.get("identificador").getAsString();
            } else if (credenciales.has("correo") && !credenciales.get("correo").isJsonNull()) {
                identificador = credenciales.get("correo").getAsString();
            }
            String contrasena = (credenciales.has("contrasena") && !credenciales.get("contrasena").isJsonNull())
                    ? credenciales.get("contrasena").getAsString()
                    : "";

            String errorValidacion = usuarioService.validarCredencialesEntrada(identificador, contrasena);
            if (errorValidacion != null) {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, errorValidacion);
                return;
            }

            Usuario usuario = usuarioService.iniciarSesion(identificador, contrasena);
            if (usuario != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                JsonObject json = new JsonObject();
                json.addProperty("exito", true);
                json.addProperty("mensaje", "Bienvenido " + usuario.getNombre());
                json.addProperty("usuarioID", usuario.getUsuarioID());
                json.addProperty("nombre", usuario.getNombre());
                json.addProperty("correo", usuario.getCorreo());
                if (usuario.getRol() != null) {
                    json.addProperty("rolID", usuario.getRol().getRolID());
                    json.addProperty("rol", usuario.getRol().getNombreRol());
                } else {
                    json.addProperty("rolID", 3);
                    json.addProperty("rol", "Cliente");
                }
                enviarJSON(response, gson.toJson(json));
            } else {
                manejarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Credenciales incorrectas");
            }
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Error al procesar el inicio de sesion: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void enviarJSON(HttpServletResponse response, String json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    private void manejarError(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        response.setStatus(codigo);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("exito", false);
        errorJson.addProperty("mensaje", mensaje);
        enviarJSON(response, gson.toJson(errorJson));
        System.err.println("ERROR LoginServlet [" + codigo + "]: " + mensaje);
    }
}
