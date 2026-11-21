package ByteBuilders.Controladores;

import ByteBuilders.Negocio.ProductoService;
import ByteBuilders.Entidad.Producto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/productos/*")
public class ProductoServlet extends HttpServlet {
    private final ProductoService productoService = new ProductoService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.isEmpty()) {
                List<Producto> productos = productoService.obtenerTodos();
                response.setStatus(HttpServletResponse.SC_OK);
                enviarJSON(response, gson.toJson(productos));
            } else {
                int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
                Producto producto = productoService.obtenerPorID(id);
                if (producto == null) {
                    manejarError(response, HttpServletResponse.SC_NOT_FOUND, "Producto no encontrado: " + id);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                enviarJSON(response, gson.toJson(producto));
            }
        } catch (NumberFormatException e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID de producto invalido");
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al consultar productos: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            Producto producto = gson.fromJson(request.getReader(), Producto.class);
            if (producto == null) {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos de producto vacios o invalidos");
                return;
            }
            boolean resultado = productoService.agregarProducto(producto);
            if (resultado) {
                enviarResultado(response, HttpServletResponse.SC_CREATED, true, "Producto agregado correctamente");
            } else {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "No se pudo agregar el producto (verifica nombre duplicado, precio o categoria)");
            }
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Error al agregar producto: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            Producto producto = gson.fromJson(request.getReader(), Producto.class);
            if (producto == null) {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Datos de producto vacios o invalidos");
                return;
            }
            boolean resultado = productoService.actualizarProducto(producto);
            if (resultado) {
                enviarResultado(response, HttpServletResponse.SC_OK, true, "Producto actualizado correctamente");
            } else {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "No se pudo actualizar el producto (verifica ID, nombre o precio)");
            }
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Error al actualizar producto: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        configurarCORS(response);
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                String pathInfo = request.getPathInfo();
                if (pathInfo != null && pathInfo.length() > 1) {
                    idStr = pathInfo.replaceFirst("/", "");
                }
            }
            if (idStr == null || idStr.isEmpty()) {
                manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "Se requiere el ID del producto a eliminar");
                return;
            }
            int id = Integer.parseInt(idStr);
            boolean resultado = productoService.eliminarProducto(id);
            if (resultado) {
                enviarResultado(response, HttpServletResponse.SC_OK, true, "Producto eliminado correctamente");
            } else {
                manejarError(response, HttpServletResponse.SC_CONFLICT, "No se pudo eliminar el producto (puede no existir o estar asociado a ventas)");
            }
        } catch (NumberFormatException e) {
            manejarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID de producto invalido");
        } catch (Exception e) {
            manejarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar producto: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private void enviarResultado(HttpServletResponse response, int codigo, boolean exito, String mensaje) throws IOException {
        response.setStatus(codigo);
        JsonObject json = new JsonObject();
        json.addProperty("exito", exito);
        json.addProperty("mensaje", mensaje);
        enviarJSON(response, gson.toJson(json));
    }

    private void enviarJSON(HttpServletResponse response, String json) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    private void manejarError(HttpServletResponse response, int codigo, String mensaje) throws IOException {
        enviarResultado(response, codigo, false, mensaje);
        System.err.println("ERROR ProductoServlet [" + codigo + "]: " + mensaje);
    }
}
